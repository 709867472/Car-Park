package cn.edu.djtu.car_park.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Frank on 15/05/2017.
 */

public class LotInfo implements Serializable {
    private int totalNumber;
    private int emptyNumber = 200;

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public int getEmptyNumber() {
        return emptyNumber;
    }

    //假设汽车开始进入
    public void start() {
        long IN = 162000;
        long OUT = 264000;
        Timer timerIn = new Timer();
        Timer timerOut = new Timer();
        TimerTask timerTaskIn = new TimerTask() {
            @Override
            public void run() {
                if (emptyNumber > 0) {
                    emptyNumber--;
                }
            }
        };
        TimerTask timerTaskOut = new TimerTask() {
            @Override
            public void run() {
                if (emptyNumber < totalNumber) {
                    emptyNumber++;
                }
            }
        };
        timerIn.schedule(timerTaskIn, IN, IN);

        timerOut.schedule(timerTaskOut, OUT, OUT);
    }
}
