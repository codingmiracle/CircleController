package com.harrysoft.androidbluetoothserial.demoapp.JoystickView;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class JoystickDataModel {
    Integer l;
    Integer r;

    private static final double turn_damping = 4;

    public Integer getL() {
        return l;
    }

    public JoystickDataModel() {
    }

    public void setL(Integer l) {
        this.l = l;
    }

    public JoystickDataModel(Integer angel, Integer power) {
        angel = angel*-1 + 90;
        double magnitude = power*10.24;

        double x = power * cos((double)angel/180*PI);
        double y = power * sin((double)angel/180*PI);
        this.l = (int)(magnitude * (sin((double)angel/180*PI) + cos((double)angel/180*PI) / turn_damping));
        this.r = (int)(magnitude * (sin((double)angel/180*PI) - cos((double)angel/180*PI) / turn_damping));;
        if(l >= 1023) {
            l = 1023;
        }
        if(r >= 1023) {
            r = 1023;
        }
        if(l <= -1024) {
            l = -1024;
        }
        if(r <= -1024) {
            r = -1024;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "l=" + l +
                ", r=" + r +
                '}' + (char)0;
    }

    public Integer getR() {
        return r;
    }

    public void setR(Integer r) {
        this.r = r;
    }
}
