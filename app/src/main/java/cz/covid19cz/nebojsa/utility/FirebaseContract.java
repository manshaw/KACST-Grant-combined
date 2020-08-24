package cz.covid19cz.nebojsa.utility;

import java.io.Serializable;
import java.util.List;

public class FirebaseContract implements Serializable {

    public long timestamp;
    public String crop;
    public String land;
    public String area;
    public List<LatLng_firebase> points;

    public FirebaseContract() {
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }


    public void setpoints(List<LatLng_firebase> points) {
        this.points = points;
    }

    public List<LatLng_firebase> getpoints() {
        return this.points;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
