package com.evgenyvyaz.cinaytaren.utils;

import com.evgenyvyaz.cinaytaren.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by X550V on 29.10.2016.
 */

public class ClassElements  {
    Map<String, Integer> elements = new HashMap<>();

    public ClassElements() {
        putInMap();
    }

    public void putInMap(){
        elements.put("banks" , R.mipmap.bank );
        elements.put("beauty shops",R.mipmap.lipstick );
        elements.put("restaurants",R.mipmap.tea_cup );
        elements.put("bars" ,R.mipmap.beer );
        elements.put("auto",R.mipmap.car );
        elements.put("supermarket",R.mipmap.online_store );
        elements.put("hotels",R.mipmap.bed );
        elements.put("gasstation",R.mipmap.gas_station );
        elements.put("dental",R.mipmap.molar );
        elements.put("airports", R.mipmap.airplane);
        elements.put("malls",R.mipmap.mall );
        elements.put("medicine",R.mipmap.medicine );
        elements.put("services",R.mipmap.payment_method );
        elements.put("drugstores",R.mipmap.antibiotic );
        elements.put("entertainments",R.mipmap.disco );
        elements.put("travel",R.mipmap.airplane_travel );
        elements.put("beauty",R.mipmap.makeup );
        elements.put("fitness", R.mipmap.dumbbell);
        elements.put("IT",R.mipmap.editor );
        elements.put("atms",R.mipmap.atm );
        elements.put("mass",R.mipmap.computer );
    }
    public  Map<String, Integer> getMapElements(){
        return elements;
    }
}
