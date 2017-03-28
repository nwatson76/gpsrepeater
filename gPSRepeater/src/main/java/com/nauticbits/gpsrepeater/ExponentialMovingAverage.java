package com.nauticbits.gpsrepeater;

//An exponential moving average (EMA) is a type of moving average that is 
//similar to a simple moving average, except that more weight is given to the latest data. 
//It's also known as the exponentially weighted moving average
//used to smooth out the instantaneous GPS speed reading from device GPS

public class ExponentialMovingAverage {
    private double alpha;
    private double dvalue;
    private double oldValue = 0;
    public ExponentialMovingAverage(double alpha) {
        this.alpha = alpha;
    }

    public String average(String value) {
    	dvalue = Double.valueOf(value.replace("°", ""));
        if (oldValue == 0) {
			//first time so set the value and return
            oldValue = dvalue;
            return String.valueOf((int)dvalue) + "°";
        }
		//all other subsequent times, set the value and return
        double newValue = oldValue + alpha * (dvalue - oldValue);
        oldValue = newValue;
        return String.valueOf((int)newValue) + "°";
        
    }
}

