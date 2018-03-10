/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import DB.dbConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Björn
 */
@WebServlet(name = "setting", urlPatterns = {"/setting"})
public class setting extends HttpServlet {

    //Deklaration der Einstellungsparameter
    String ip, port, intervall;
    Integer[] adc = new Integer[8];

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
        
        try {
            //daten aus der DB laden und den Einstellungsparametern zuweisen
            loadData();

        } catch (SQLException ex) {
            try (PrintWriter out = response.getWriter()) {
                //Fehlermeldung ausgeben
                out.println("<h1>Fehler beim Laden der Konfigurationsdaten</h1>");
            }
        }
        try (PrintWriter out = response.getWriter()) {
            out.println(buildHTML(request));
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

        System.out.println("doPost wird ausgeführt");

        //Button "save" gedrückt
        if (request.getParameter("save") != null) {

            //Parameter aus Input Feldern übernehmen
            this.ip = request.getParameter("ip");
            this.port = request.getParameter("port");
            this.intervall = request.getParameter("intervall");

            //Einlesen der gecheckten ADC
            for (int i = 0; i < 8; i++) {
                int checked = 0;
                //prüfen ob Checkbox gechecked ist
                if (String.class.isInstance(request.getParameter("adcCheck" + i))) {
                    checked = 1;
                }
                this.adc[i] = checked;
            }

            try {
                //Daten in die DB abspeichern
                saveData();
            } catch (SQLException ex) {
                System.out.println("Fehler beim speichern der Daten in die Datenbank. Fehler: " + ex.getMessage());
            }
        }

        //Seite neu bauen
        processRequest(request, response);
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

    /**
     * lädt Einstellungsparameter aus der Datenbank und initialisiert diese
     */
    private void loadData() throws SQLException {
        dbConnection con = new dbConnection();
        ResultSet rs;
        
        //DB Abfrage um die Einstellungsparameters des Daemons zu bearbeiten
        rs = con.executeQuery("SELECT * FROM daemonsettings");
        while (rs.next()) {
            this.ip = rs.getString("IPAdresse");
            this.port = rs.getString("Port");
            this.intervall = rs.getString("Intervall");
        }

        //DB Abfrage um die Zugewiesenen Sensoren abzufragen
        rs = con.executeQuery("SELECT * FROM sensoren");
        int index = 0;
        //Einteilung der zugewiesenen Sensoren in Aktiv und Inaktive Sensorenanschlüsse
        while (rs.next()) {
            adc[index] = rs.getInt("active");
            index++;
        }
        con.close();
    }

    /**
     * Speichert Einstellungsparameter in der Datenbank ab
     */
    private void saveData() throws SQLException {
        dbConnection con = new dbConnection();
        con.executeUpdate("UPDATE daemonsettings SET IPAdresse='" + this.ip + "', Port='" + this.port + "', Intervall=" + this.intervall + " WHERE ID = 1");

        for (int i = 0; i < this.adc.length; i++) {
            con.executeUpdate("UPDATE sensoren SET Active=" + this.adc[i] + " WHERE ADCPort = " + i);
        }
    }

    /**
     * Baut das HTML Template des Servlets zusammen.
     *
     * @param request servlet request
     * @return HTML Template
     */
    private String buildHTML(HttpServletRequest request) {

        //Start des HTML DOM
        String htmlStart = "<!DOCTYPE html>\n"
                + "<html lang=\"de\">";

        //HTML Header
        String htmlHead = "<head>\n"
                + "  <meta charset=\"utf-8\" />\n"
                + "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"assets/fav.ico\">"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
                + "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.3/css/bootstrap.min.css\" integrity=\"sha384-Zug+QiDoJOrZ5t4lssLdxGhVrurbmBWopoEl+M6BdEfwnCJZtKxi1KgxUyJq13dy\"\n"
                + "    crossorigin=\"anonymous\">\n"
                + "  <script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\"\n"
                + "    crossorigin=\"anonymous\"></script>\n"
                + "  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\"\n"
                + "    crossorigin=\"anonymous\"></script>\n"
                + "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.3/js/bootstrap.min.js\" integrity=\"sha384-a5N7Y/aK3qNeh15eJKGWxsqtnX/wWdSZSKp+81YjTmS15nvnvxKHuzaWwXHDli+4\"\n"
                + "    crossorigin=\"anonymous\"></script>\n"
                + "<script defer src=\"https://use.fontawesome.com/releases/v5.0.6/js/all.js\"></script>"
                + "  <link rel=\"stylesheet\" href=\"" + request.getContextPath() + "/styles/footer.css\">\n"
                + "  <link rel=\"stylesheet\" href=\"" + request.getContextPath() + "/styles/settings.css\">\n"
                + "  <title>Einstellungen</title>\n"
                + "</head>";

        //HTML Body
        String htmlBody = "<body>\n"
                + "  <nav class=\"navbar navbar-dark bg-dark\">\n"
                + "    <span class=\"navbar-text\">\n"
                + "<i class=\"fas fa-cogs\"></i>"
                + "            Einstellungen\n"
                + "          </span>\n"
                + "  </nav>\n"
                + "  <div class=\"container\">\n"
                + "    <div class=\"row\">\n"
                + "      <div class=\"col\">\n"
                + "        <div class=\"card\">\n"
                + "          <div class=\"card-body\">\n"
                + "            <h3 class=\"card-title\">Daemon Einstellungen</h3>\n"
                + "              <form action=\"" + request.getContextPath() + "/setting\" method=\"post\">\n"
                + "            <table class=\"table table-hover\">\n"
                + "              <thead>\n"
                + "                <tr>\n"
                + "                  <th class=\"tableleft\" scope=\"col\">Parameter</th>\n"
                + "                  <th class=\"tableright\" scope=\"col\">Wert</th>\n"
                + "                </tr>\n"
                + "              </thead>\n"
                + "              <tbody>\n"
                + "                <tr>\n"
                + "                  <td class=\"tableleft\" >Controller IP Adresse</td>\n"
                + "                  <td class=\"tableright\"><input type=\"text\" name=\"ip\" value=\"" + ip + "\"></td>\n"
                + "                </tr>\n"
                + "                <tr>\n"
                + "                  <td class=\"tableleft\">Controller Port</td>\n"
                + "                  <td class=\"tableright\"><input type=\"number\" name=\"port\" value=\"" + this.port + "\"></td>\n"
                + "                </tr>\n"
                + "                <tr>\n"
                + "                  <td class=\"tableleft\">Intervall in Sekunden</td>\n"
                + "                  <td class=\"tableright\"><input type=\"number\" name=\"intervall\" value=\"" + this.intervall + "\"></td>\n"
                + "                </tr>\n"
                + "              </tbody>\n"
                + "            </table>\n"
                + "<div class=\"row\" style=\"margin-top: 30px;\">\n"
                + "              <h3 class=\"adcHead\" >ADC Auswahl</h3>\n"
                + "            </div>\n"
                + "              <hr>\n"
                + "              <div class=\"row\">\n";

        //ADC Auswahl
        String adcSelection = "";
        for (int i = 0; i < this.adc.length; i++) {
            adcSelection += "<div class=\"col-sm\">"
                    + "<div class=\"custom-control custom-checkbox\">";

            //Prüfen ob gecheckt
            if (this.adc[i]==1) {
                adcSelection += "<input type=\"checkbox\" class=\"custom-control-input\" name=\"adcCheck" + i + "\" id=\"adcCheck" + i + "\" checked >";
            } else {
                adcSelection += "<input type=\"checkbox\" class=\"custom-control-input\" name=\"adcCheck" + i + "\" id=\"adcCheck" + i + "\">";
            }

            adcSelection += " <label class=\"custom-control-label\" for=\"adcCheck" + i + "\">ADC" + i + "</label>\n"
                    + "                        </div>\n"
                    + "                    </div>\n";

        }

        htmlBody += adcSelection;

        htmlBody += "              </div>\n"
                + "              \n"
                + "            <div class=\"row\">\n"
                + "              <div class=\"col\">\n"
                + "                <a href=\"/MC_Projekt/\" class=\"btn btn-danger\">"
                + "<i class=\"fas fa-arrow-circle-left fa-lg\"></i></a>\n"
                + "              </div>\n"
                + "              <div class=\"col\">\n"
                + "                   <button type=\"submit\" class=\"btn btn-success\" name=\"save\" value=\"save\">"
                + "<i class=\"far fa-save fa-lg\"></i></button>\n"
                + "               </div>\n"
                + "            </div>"
                + "            </div>\n"
                + "           </form>\n"
                + "          </div>\n"
                + "        </div>\n"
                + "      </div>\n"
                + "    </div>\n"
                + "  </div>\n";
        
         String htmlfooter = "<footer class=\"footer-distributed\">\n"
                + "\n"
                + "			<div class=\"footer-right\">\n"
                + "\n"
                + "				<a href=\"#\"><i class=\"fab fa-facebook-square\"></i></a>\n"
                + "				<a href=\"#\"><i class=\"fab fa-twitter-square\"></i></a>\n"
                + "				<a href=\"#\"><i class=\"fab fa-linkedin\"></i></a>\n"
                + "				<a href=\"#\"><i class=\"fab fa-git-square\"></i></a>\n"
                + "\n"
                + "			</div>\n"
                + "\n"
                + "			<div class=\"footer-left\">\n"
                + "\n"
                + "				<p class=\"footer-links\">\n"
                + "					<a href=\" "+ request.getContextPath()+"\">Home</a>\n"
                + "					·\n"
                + "					<a href=\""+ request.getContextPath()+"/dashboard\">Dashboard</a>\n"
                + "					·\n"
                + "				</p>\n"
                + "\n"
                + "				<p>Company Name &copy; 2015</p>\n"
                + "			</div>\n"
                + "\n"
                + "		</footer>";

        
              
        //Ende des HTML DOM
        String htmlEnd = "</body></html>";

        System.out.println("html wird gebaut");
        return htmlStart + htmlHead + htmlBody +htmlfooter +htmlEnd;
    }

}
