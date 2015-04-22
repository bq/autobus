package com.bq.autobus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by imartinez on 13/04/15.
 */
public class BusDataStubListenerWithPreprocessor extends BusListener<BusDataStub> {

    private ArrayList<BusDataStub> busDataList = new ArrayList<BusDataStub>();

    public BusDataStubListenerWithPreprocessor(Class<BusDataStub> expectedDataClass, Preprocessor<BusDataStub> preprocessor) {
        super(expectedDataClass, preprocessor);
    }

    public static BusDataStubListenerWithPreprocessor getNewBusDataStubListener() {
        return new BusDataStubListenerWithPreprocessor(BusDataStub.class, new BusDataDuplicatorBusPreprocessor<BusDataStub>());
    }

    @Override
    public void notifyEvent(@NotNull BusDataStub busData) {
        busDataList.add(busData);
    }

    public int getReceivedBusDataCount() {
        return busDataList.size();
    }
}
