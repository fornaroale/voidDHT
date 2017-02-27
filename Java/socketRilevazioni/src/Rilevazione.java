
/**
 *
 * @author fornaro_alessandro
 */
public class Rilevazione implements java.io.Serializable {
    private float temp;
    private float hum;
    
    public Rilevazione(){
        temp=0;
        hum=0;
    }
    
    public Rilevazione(float temp, float hum){
        this.temp=temp;
        this.hum=hum;
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
    
    public String toString() {
        return (this.temp + "," + this.hum);
    }
}
