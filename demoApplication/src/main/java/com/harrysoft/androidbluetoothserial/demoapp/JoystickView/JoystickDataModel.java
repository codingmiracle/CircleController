package com.harrysoft.androidbluetoothserial.demoapp.JoystickView;

public class JoystickDataModel {
    Integer l;
    Integer r;

    public Integer getL() {
        return l;
    }

    public JoystickDataModel() {
    }

    public void setL(Integer l) {
        this.l = l;
    }

    public JoystickDataModel(Integer angel, Integer power) {
        this.l = (int)(power * 10.24 * Math.sin(angel));
        this.r = (int)(power * 10.24 * Math.cos(angel));
    }

    @Override
    public String toString() {
        return "{" +
                "l=" + l +
                ", r=" + r +
                '}';
    }

    public Integer getR() {
        return r;
    }

    public void setR(Integer r) {
        this.r = r;
    }
}
