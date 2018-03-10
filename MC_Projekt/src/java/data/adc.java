/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.ArrayList;

/**
 * @author Björn
 */
public class adc {
    int port;
    temperatur currentTemp;
    ArrayList<temperatur> tempList;

    

    /**
     * Datenobjekt für ADC Werte
     * @param port auf welchem Port der ADC am Controller angeschlossen ist
     */
    public adc(int port) {
        this.port = port;
    }
    
    /**
     * Datenobjekt für ADC Werte
     * @param port auf welchem Port der ADC am Controller angeschlossen ist
     * @param currentTemp Aktueller Temperaturwert des ADC
     * @param tempList ArrayListe mit historischen Temperaturwerten des ADC
     */
    public adc(int port, temperatur currentTemp, ArrayList<temperatur> tempList) {
        this.port = port;
        this.currentTemp = currentTemp;
        this.tempList = tempList;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ArrayList<temperatur> getTempList() {
        return tempList;
    }

    public void setTempList(ArrayList<temperatur> tempList) {
        this.tempList = tempList;
    }
    
    public temperatur getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(temperatur currentTemp) {
        this.currentTemp = currentTemp;
    }
    
}
