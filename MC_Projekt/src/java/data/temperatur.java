/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.sql.Date;
import java.sql.Time;
import java.text.DecimalFormat;

/**
 *
 * @author Björn
 */
public class temperatur {
    private double temp;
    private Date date;
    private Time time;
    
    /**
     * Datenobjekt für einen Temperaturwert eines ADC
     * @param temp Temperaturwert
     * @param date Datum an dem der Wert gemessen wurde
     * @param time Uhrzeit an dem der Wert gemessen wurde
     */
    public temperatur(double temp,Date date, Time time)
    {
        //Double auf zwei Nachkommastellen formatieren
        DecimalFormat temperaturDouble = new DecimalFormat("#.##");
        String temperaturString = temperaturDouble.format(temp);
        //Anpassung des Temperaturwerts um diesen zu Konvertieren
        temperaturString = temperaturString.replace(",", ".");
        this.temp = Double.valueOf(temperaturString);
        
        this.date = date;
        this.time = time;
    }

    public double getTemp() {
        return temp;
    }


    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }
}
