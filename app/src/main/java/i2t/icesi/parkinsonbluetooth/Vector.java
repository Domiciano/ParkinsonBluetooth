package i2t.icesi.parkinsonbluetooth;

/**
 * Created by Domiciano on 25/04/2018.
 */
public class Vector {
    public int millis;
    public int x;
    public int y;
    public int z;
    public int t = 0;

    public Vector(){}
    public Vector(int millis, int x, int y, int z){
        this.millis = millis;
        this.x = x;
        this.y= y;
        this.z= z;
    }
}
