package dev.artem.mostransport.models;

public class Street {
    private String id;
    private String pic;
    private String sensQuant;
    private String longitude;
    private String latitude;
    private String active;
    private String street_name;
    private String street_type;

    public Street(String id, String pic, String sensQuant, String longitude, String latitude, String active, String street_name, String street_type) {
        this.id = id;
        this.pic = pic;
        this.sensQuant = sensQuant;
        this.longitude = longitude;
        this.latitude = latitude;
        this.active = active;
        this.street_name = street_name;
        this.street_type = street_type;
    }

    public Street() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getSensQuant() {
        return sensQuant;
    }

    public void setSensQuant(String sensQuant) {
        this.sensQuant = sensQuant;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public String getStreet_type() {
        return street_type;
    }

    public void setStreet_type(String street_type) {
        this.street_type = street_type;
    }
}
