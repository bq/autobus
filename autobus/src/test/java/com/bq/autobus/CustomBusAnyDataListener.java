package com.bq.autobus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by imartinez on 13/04/15.
 */
public class CustomBusAnyDataListener extends BusAnyDataListener {

    private ArrayList<Object> busDataList = new ArrayList<>();

    public CustomBusAnyDataListener() {
        super();
    }

    public static CustomBusAnyDataListener getNewBusAnyDataListener() {
        return new CustomBusAnyDataListener();
    }

    @Override
    public void notifyEvent(@NotNull Object busData) {
        busDataList.add(busData);
    }

    public int getReceivedBusDataCount() {
        return busDataList.size();
    }
}
