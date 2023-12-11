package com.drt.moisture.measure;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name = "预测结果")
public class BarometricPressure {
    @SmartColumn(id = 1, name = "温度/℃")
    private int temperature;


    @SmartColumn(id = 2, name = "压力/MPa")
    private String pressure;

    public BarometricPressure() {

    }

    public BarometricPressure(int temperature, String pressure) {
        this.temperature = temperature;
        this.pressure = pressure;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
}
