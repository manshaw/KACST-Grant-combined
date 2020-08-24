package cz.covid19cz.nebojsa.utility;

import java.io.Serializable;

public class LatLng_firebase implements Serializable {
    private Double latitude;
    private Double longitude;

    public LatLng_firebase() {

    }

    public LatLng_firebase(Double Lat, Double Long) {
        this.latitude = Lat;
        this.longitude = Long;
    }


    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
