package com.bq.autobus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by imartinez on 13/04/15.
 */
public class BusDataStubListener extends BusListener<BusDataStub> {

    private ArrayList<BusDataStub> busDataList = new ArrayList<BusDataStub>();

    public BusDataStubListener(Class<BusDataStub> expectedDataClass) {
        super(expectedDataClass);
    }

    public static BusDataStubListener getNewBusDataStubListener() {
        return new BusDataStubListener(BusDataStub.class);
    }

    @Override
    public void notifyEvent(@NotNull BusDataStub busData) {
        busDataList.add(busData);
    }

    public int getReceivedBusDataCount() {
        return busDataList.size();
    }
}
