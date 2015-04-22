package com.bq.autobus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by imartinez on 13/04/15.
 */
public class BusDataStubExtensionListener extends BusListener<BusDataStubExtension> {

    private ArrayList<BusDataStubExtension> busDataList = new ArrayList<BusDataStubExtension>();

    public BusDataStubExtensionListener(Class<BusDataStubExtension> expectedDataClass) {
        super(expectedDataClass);
    }

    public static BusDataStubExtensionListener getNewBusDataStubExtensionListener() {
        return new BusDataStubExtensionListener(BusDataStubExtension.class);
    }

    @Override
    public void notifyEvent(@NotNull BusDataStubExtension busData) {
        busDataList.add(busData);
    }

    public int getReceivedBusDataCount() {
        return busDataList.size();
    }

}
