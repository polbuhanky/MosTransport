package dev.artem.mostransport.models;

public class Mark {
    private String sim_id;
    private String sim_imei;
    private String sim_ccid;
    private String street_id;
    private String defect;
    private String sens_lat;
    private String sens_long;
    private String active;
    private String current_firmware;
    private String latest_firmware;
    private String firmware_gettime;
    private String settings_time;
    private String new_settings_time;
    private String request_settings;
    private String volt_phone;
    private String rssi_phone;
    private String flag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public Mark() {
    }

    public Mark(String sim_id, String sim_imei, String sim_ccid, String street_id, String defect, String sens_lat, String sens_long, String active, String current_firmware, String latest_firmware, String firmware_gettime, String settings_time, String new_settings_time, String request_settings, String volt_phone, String rssi_phone, String flag) {
        this.sim_id = sim_id;
        this.sim_imei = sim_imei;
        this.sim_ccid = sim_ccid;
        this.street_id = street_id;
        this.defect = defect;
        this.sens_lat = sens_lat;
        this.sens_long = sens_long;
        this.active = active;
        this.current_firmware = current_firmware;
        this.latest_firmware = latest_firmware;
        this.firmware_gettime = firmware_gettime;
        this.settings_time = settings_time;
        this.new_settings_time = new_settings_time;
        this.request_settings = request_settings;
        this.volt_phone = volt_phone;
        this.rssi_phone = rssi_phone;
        this.flag = flag;
    }

    public String getSim_id() {
        return sim_id;
    }

    public void setSim_id(String sim_id) {
        this.sim_id = sim_id;
    }

    public String getSim_imei() {
        return sim_imei;
    }

    public void setSim_imei(String sim_imei) {
        this.sim_imei = sim_imei;
    }

    public String getSim_ccid() {
        return sim_ccid;
    }

    public void setSim_ccid(String sim_ccid) {
        this.sim_ccid = sim_ccid;
    }

    public String getStreet_id() {
        return street_id;
    }

    public void setStreet_id(String street_id) {
        this.street_id = street_id;
    }

    public String getDefect() {
        return defect;
    }

    public void setDefect(String defect) {
        this.defect = defect;
    }

    public String getSens_lat() {
        return sens_lat;
    }

    public void setSens_lat(String sens_lat) {
        this.sens_lat = sens_lat;
    }

    public String getSens_long() {
        return sens_long;
    }

    public void setSens_long(String sens_long) {
        this.sens_long = sens_long;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCurrent_firmware() {
        return current_firmware;
    }

    public void setCurrent_firmware(String current_firmware) {
        this.current_firmware = current_firmware;
    }

    public String getLatest_firmware() {
        return latest_firmware;
    }

    public void setLatest_firmware(String latest_firmware) {
        this.latest_firmware = latest_firmware;
    }

    public String getFirmware_gettime() {
        return firmware_gettime;
    }

    public void setFirmware_gettime(String firmware_gettime) {
        this.firmware_gettime = firmware_gettime;
    }

    public String getSettings_time() {
        return settings_time;
    }

    public void setSettings_time(String settings_time) {
        this.settings_time = settings_time;
    }

    public String getNew_settings_time() {
        return new_settings_time;
    }

    public void setNew_settings_time(String new_settings_time) {
        this.new_settings_time = new_settings_time;
    }

    public String getRequest_settings() {
        return request_settings;
    }

    public void setRequest_settings(String request_settings) {
        this.request_settings = request_settings;
    }

    public String getVolt_phone() {
        return volt_phone;
    }

    public void setVolt_phone(String volt_phone) {
        this.volt_phone = volt_phone;
    }

    public String getRssi_phone() {
        return rssi_phone;
    }

    public void setRssi_phone(String rssi_phone) {
        this.rssi_phone = rssi_phone;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
