/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import DB.dbConnection;
import data.adc;
import data.temperatur;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import its.html.basic.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 *
 * @author Björn
 */
@WebServlet(name = "dashboard", urlPatterns = {"/dashboard"})
public class dashboard extends HttpServlet {

    //List der ADC die am Controller verfügbar sind
    ArrayList<adc> adcList;

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config); //To change body of generated methods, choose Tools | Templates.
    }
     
   /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try {
                adcList = new ArrayList<>();
                //Laden der Aktiven ADC's
                loadADC();
                //Teperaturwerte der Aktiven ADC's laden
                loadTempFromADC();
            } catch (SQLException ex) {
                System.out.println("Beim Abfragen der DB ist ein Fehler aufgetretten: " + ex.getMessage());
            }
            out.println(buildHtml(request));
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * lädt Aktive ADC Ports aus der Datenbank
     */
    private void loadADC() throws SQLException {
        dbConnection con = new dbConnection();
        ResultSet rs;
        rs = con.executeQuery("SELECT ADCPort FROM sensoren Where Active = 1;");
        while (rs.next()) {

            adcList.add(new adc(rs.getInt("ADCPort")));
        }
        con.close();
    }

    /**
     * lädt Aktive ADC Ports aus der Datenbank
     */
    private void loadTempFromADC() throws SQLException {
        dbConnection con = new dbConnection();
        ResultSet rs;

        //durch alle Aktiven ADC's iterrieren
        for (adc port : adcList) {
            //Für jeden ADC die aktuelle Temperatur abfragen
            rs = con.executeQuery("SELECT * FROM temperatur Where ADCPort = " + port.getPort() + " ORDER BY ID DESC LIMIT 1;");
            while (rs.next()) {
                //Temperatur zuweisen
                temperatur temp = new temperatur(rs.getDouble("Temperatur"), rs.getDate("Day"), rs.getTime("Time"));
                port.setCurrentTemp(temp);

            }

            //Für jeden ADC die letzen 5 Temperaturwerte abfragen
            rs = con.executeQuery("SELECT * FROM temperatur Where ADCPort = " + port.getPort() + " ORDER BY ID DESC LIMIT 5;");

            //Deklaration und Initialisierung einer Liste von Temperaturen um die letzten Temperaturwerte eines ADC zu speichern
            ArrayList<temperatur> tempList = new ArrayList<>();

            while (rs.next()) {
                temperatur temp = new temperatur(rs.getDouble("Temperatur"), rs.getDate("Day"), rs.getTime("Time"));
                tempList.add(temp);
            }

            //templist mit Historischen Temperaturwerten dem aktuellen ADC zuweisen
            port.setTempList(tempList);
        }

        con.close();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private String buildHtml(HttpServletRequest request) {
        //Start des HTML DOM
        String htmlStart = "<!DOCTYPE html>\n"
                + "<html lang=\"de\">";

        //HTML Header
        String htmlHead = "<head>\n"
                + "  <meta charset=\"utf-8\" />\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
                + "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"assets/fav.ico\">"
                + "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.3/css/bootstrap.min.css\" integrity=\"sha384-Zug+QiDoJOrZ5t4lssLdxGhVrurbmBWopoEl+M6BdEfwnCJZtKxi1KgxUyJq13dy\"\n"
                + "    crossorigin=\"anonymous\">\n"
                + "  <script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\"\n"
                + "    crossorigin=\"anonymous\"></script>\n"
                + "  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\"\n"
                + "    crossorigin=\"anonymous\"></script>\n"
                + "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.3/js/bootstrap.min.js\" integrity=\"sha384-a5N7Y/aK3qNeh15eJKGWxsqtnX/wWdSZSKp+81YjTmS15nvnvxKHuzaWwXHDli+4\"\n"
                + "    crossorigin=\"anonymous\"></script>\n"
                + "<script defer src=\"https://use.fontawesome.com/releases/v5.0.6/js/all.js\"></script>"
                + "  <link rel=\"stylesheet\" href=\"" + request.getContextPath() + "/styles/dashboard.css\">\n"
                + "  <link rel=\"stylesheet\" href=\"" + request.getContextPath() + "/styles/footer.css\">\n"
                + "<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>"
                + "  <title>Dashboard</title>\n"
                + "</head>";

        //HTML Body
        String htmlNav = "<body>"
                + "<nav class=\"navbar navbar-dark fixed-top bg-dark\">\n"
                + "    <span class=\"navbar-text\">\n"
                + "<i class=\"fas fa-desktop\"></i>\n"
                + "            Dashboard\n"
                + "          </span>\n"
                + "  </nav>";

        String htmlContainerStart = "<div class=\"container\">\n"
                + "\n"
                + "    <div class=\"row\">\n"
                + "      <div class=\"col\">\n"
                + "        <div class=\"card cardcontainer\">"
                + "          <div class=\"card-body\">"
                + "<div class=\"row\">\n";

        String htmlADC = "";

        //Auflistung der ADC
        for (adc adc : adcList) {
            htmlADC += "              <div class=\"col\">\n"
                    + "                <div class=\"card card-center\" style=\"width: 200px; height:290px\">\n"
                    + "                  <div class=\"card-body \">\n"
                    + "                    <div class=\"card-title text-center\">\n"
                    + "                      <h3>ADC "
                    + adc.getPort()
                    + "</h3>\n"
                    + "                    </div>\n"
                    + "                    <div class=\"row\">\n"
                    + "                      <div class=\"col\">\n"
                    + "                        <i class=\"fas fa-thermometer-empty fa-5x tempimg\"></i>\n"
                    + "                      </div>\n"
                    + "                    </div>\n"
                    + "                    <div class=\"row\">\n"
                    + "                      <div class=\"col\">\n"
                    + "                          <div class=\"title\">\n"
                    + "                              <div class=\"unit text-center\">\n"
                    + "                                Temperatur\n"
                    + "                              </div>\n"
                    + "                          </div>\n"
                    + "                      </div>\n"
                    + "                    </div>\n"
                    + "                    <div class=\"row\">\n"
                    + "                      <div class=\"col text-center\">\n"
                    + "                          <div class=\"number\">"
                    + adc.getCurrentTemp().getTemp()
                    + "                          </div>\n"
                    + "                          <div class=\"measure\">Grad</div>\n"
                    + "                          </div>\n"
                    + "                     </div>\n"
                    + "                  </div>\n"
                    + "                </div>\n"
                    + "              </div>\n";
        }

        htmlADC += "</div>";

        String htmlContainerEnd = "             <div class=\"row\">\n"
                + "              <div class=\"col\">\n"
                + "                <a href=\"javascript:history.go(0)\" class=\"btn btn-success\"><i class=\"fas fa-sync-alt fa-lg\"></i></a>\n"
                + "                <a href=\""+request.getContextPath()+"\" class=\"btn btn-danger\"><i class=\"fas fa-arrow-circle-left fa-lg\"></i></a>\n"
                + "              </div>\n"
                + "            </div>\n"
                + "          </div>\n"
                + "          </div>\n"
                + "          </div>\n"
                + "          </div>\n"
                + "        </div>";
        String htmlCharttest = "<div class=\"row mx-auto\"><div class=\"col mx-auto\"><div id=\"curve_chart\" style=\"width: 900px; height: 500px\"></div></div></div>";
        String htmlCharts = "<script type=\"text/javascript\">\n"
                + "      google.charts.load('current', {'packages':['corechart']});\n"
                + "      google.charts.setOnLoadCallback(drawChart);\n"
                + "\n"
                + "      function drawChart() {\n"
                + "        var data = google.visualization.arrayToDataTable([\n";

        //Headbereich des ChartDataArrays erstellen
        //Wie viele ADC's sollen angezeigt werden
        String chartData = "['Uhrzeit',";
        for (adc adc : adcList) {
            chartData += "'adc " + adc.getPort() + "',";
        }
        chartData = chartData.substring(0, chartData.length() - 1);
        chartData += "],";

        //Wertebereich des ChartDataArrays erstellen
        //Wie viele Historische Werte sollen angezeigt werden
        for (int i = 4; i >= 0; i--) {

            boolean timestamp = false;
            for (adc adc : adcList) {

                //Prüfen ob Zeitstempel hinfzugefügt wurde
                if (!timestamp) {
                    //Zeitstempel hinzufügen
                    chartData += "['" + adcList.get(0).getTempList().get(i).getTime() + "',";
                }
                timestamp = true;
                //Hinzufügen des Temperaturwerts aus der Historie
                chartData += "" + adc.getTempList().get(i).getTemp() + ",";
            }

            chartData = chartData.substring(0, chartData.length() - 1);
            chartData += "],";

        }
        chartData = chartData.substring(0, chartData.length() - 1);

        htmlCharts += chartData;
        htmlCharts += "]);\n"
                + "\n"
                + "        var options = {\n"
                + "          title: 'Temperaturwerte',\n"
                + "          curveType: 'function',\n"
                + "          legend: { position: 'bottom' }\n"
                + "        };\n"
                + "\n"
                + "        var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));\n"
                + "\n"
                + "        chart.draw(data, options);\n"
                + "      }\n"
                + "    </script>";

        String htmlfooter = "<footer class=\"footer-distributed\">\n"
                + "\n"
                + "			<div class=\"footer-right\">\n"
                + "\n"
                + "				<a href=\"#\"><i class=\"fab fa-facebook-square\"></i></a>\n"
                + "				<a href=\"#\"><i class=\"fab fa-twitter-square\"></i></a>\n"
                + "				<a href=\"#\"><i class=\"fab fa-linkedin\"></i></a>\n"
                + "				<a href=\"https://github.com/Beeerrt/Temp_Dashboard\"><i class=\"fab fa-git-square\"></i></a>\n"
                + "\n"
                + "			</div>\n"
                + "\n"
                + "			<div class=\"footer-left\">\n"
                + "\n"
                + "				<p class=\"footer-links\">\n"
                + "					<a href=\" "+ request.getContextPath()+"\">Home</a>\n"
                + "					·\n"
                + "					<a href=\""+ request.getContextPath()+"/setting\">Settings</a>\n"
                + "					·\n"
                + "				</p>\n"
                + "\n"
                + "				<p>Goldener Janni &copy; 2018</p>\n"
                + "			</div>\n"
                + "\n"
                + "		</footer>";

        String htmlEnd = "</body></html>";
        String html = htmlStart + htmlHead + htmlNav + htmlContainerStart + htmlADC + htmlCharttest + htmlCharts + htmlContainerEnd + htmlfooter + htmlEnd;
        return html;
    }

}
