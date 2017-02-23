/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javatemperatura;

/**
 *
 * @author fornaro_alessandro
 */
public class Rilevazione {
    private float temp;
    private float hum;
    
    public Rilevazione(){
        temp=0;
        hum=0;
    }
    
    public void setData(float temp, float hum){
        this.temp=temp;
        this.hum=hum;
    }
    
    public float getTemp() {
        return this.temp;
    }
    
    public float getHum() {
        return this.hum;
    }
}
