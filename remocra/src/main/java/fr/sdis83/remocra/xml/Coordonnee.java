package fr.sdis83.remocra.xml;

public class Coordonnee {

    private double longitude;

    private double latitude;

    public Coordonnee() {
        //
    }

    public Coordonnee(double longitude, double latitute) {
        this.longitude = longitude;
        this.latitude = latitute;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
