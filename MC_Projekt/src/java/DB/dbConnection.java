 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author Björn
 */
public final class dbConnection {

    Connection connection = null;
    Statement stat = null;
    ResultSet rs = null;
    String dbIP,dbPort,dbName,dbUser,dbPW;
    

    public dbConnection() {
        establishConnection();
    }

    private void establishConnection() {
        //Treiber-Klasse laden
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Fehler: " + ex.getMessage());
        }
        //Verbindung aufbauen
        try {
            loadConfigFile();
            connection = DriverManager.getConnection("jdbc:mysql://" + this.dbIP + ":"+this.dbPort +"/"+ this.dbName ,  this.dbUser , this.dbPW );
        } catch (SQLException | IOException ex) {
            System.out.println("Fehler: " + ex.getMessage());
        }
    }

    public ResultSet executeQuery(String query) {
        //SQL-Statement an den Server schicken
        try {
            stat = connection.createStatement();//Statement erzeugen
            stat.executeQuery(query);// Statement ausführen
        } catch (SQLException ex) {
            System.out.println("Fehler: " + ex.getMessage());
        }
        //Resultset vom Server abholen
        try {
            rs = stat.getResultSet();
        } catch (SQLException ex) {
            System.out.println("Fehler: " + ex.getMessage());
        }

        return rs;
    }

    public int executeUpdate(String update) throws SQLException {
        stat = connection.createStatement();
        return stat.executeUpdate(update);
    }

    public void close() throws SQLException {
        rs.close();
        stat.close();
    }

    private void closeConnection() throws SQLException {
        connection.close();
    }

    private void loadConfigFile() throws IOException {
        Properties prop = new Properties();
        InputStream input;
        String config = "C:\\Users\\Björn\\Documents\\NetBeansProjects\\homepage\\MC_Projekt\\src\\java\\config\\config.properties";
        input = new FileInputStream(config);

        if (input != null) {
            prop.load(input);
        } else {
            throw new FileNotFoundException("property file 'config' not found in the classpath");
        }

        this.dbIP = prop.getProperty("dbip");
        this.dbPort = prop.getProperty("dbport");
        this.dbName = prop.getProperty("dbname");
        this.dbPW = prop.getProperty("dbpw");
        this.dbUser = prop.getProperty("dbuser");
    }
}
