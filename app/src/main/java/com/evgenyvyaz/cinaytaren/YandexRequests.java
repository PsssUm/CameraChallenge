package com.evgenyvyaz.cinaytaren;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.evgenyvyaz.cinaytaren.preferences.Preferences;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by X550V on 29.10.2016.
 */

public class YandexRequests {
    public interface OnYandexPlacesListener {
        public void success(JSONObject jsonObject);
        public void failure();
    }
    private static final String HTTP_URL_YANDEX = "https://search-maps.yandex.ru/v1/?ll=";
    private static final String SPN = "&spn=";
    private static final String API_KEY = "&lang=ru_RU&apikey=b3155091-8cdf-43cb-bcf8-227eb9a6c752";
  //  https://search-maps.yandex.ru/v1/?text=деревня Пожарище&ll=40.17248,60.594641&spn=3.552069,2.400552&lang=ru_RU&apikey=<API-ключ>
    public static void requestGetPlaces(final OnYandexPlacesListener onYandexPlacesListener, final Context context) {
        final Handler handler = new Handler(Looper.getMainLooper());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(HTTP_URL_YANDEX + Preferences.getMyLong(context) + "," + Preferences.getMyLat(context) + SPN + "0.005069,0.0050552" + API_KEY);

                    System.out.println("url = " + url);
                    sendRequest(onYandexPlacesListener, url, "GET");
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onYandexPlacesListener.failure();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    private static void sendRequest(final OnYandexPlacesListener onYandexPlacesListener, URL url, String Method) {
        final Handler handler = new Handler(Looper.getMainLooper());
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(Method);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                final JSONObject responseJson = new JSONObject(responseOutput.toString());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onYandexPlacesListener.success(responseJson);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onYandexPlacesListener.failure();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            onYandexPlacesListener.failure();
        }
    }
}
