package com.evgenyvyaz.cinaytaren.utils;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

public class TimerSearchBreaker {

    private Context act;

    public interface ISearchTask {
        public void searchUpdate(float degrees);
    }

    private ISearchTask searchTask;
    private Timer timer;

    public TimerSearchBreaker(ISearchTask searchTask) {
        this.searchTask = searchTask;
    }


    public void run(int time, float degrees) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        TimerTask updateBall = new UpdateSearchTask(degrees);
        timer.schedule(updateBall, time);
    }

    class UpdateSearchTask extends TimerTask {

        float degrees;

        public UpdateSearchTask(float degrees) {

            this.degrees = degrees;

        }

        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    searchTask.searchUpdate(degrees);
                }
            });
        }
    }
}