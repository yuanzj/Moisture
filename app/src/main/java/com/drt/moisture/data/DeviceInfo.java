package com.drt.moisture.data;

public class DeviceInfo {


    private String SN;

    private String version;

    private String model;

    private String name;

    private String battery;

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "SN='" + SN + '\'' +
                ", version='" + version + '\'' +
                ", model='" + model + '\'' +
                ", name='" + name + '\'' +
                ", battery='" + battery + '\'' +
                '}';
    }
}
