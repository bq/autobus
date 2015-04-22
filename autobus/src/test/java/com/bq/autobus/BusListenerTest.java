package com.bq.autobus;

import junit.framework.TestCase;

public class BusListenerTest extends TestCase {

    private CustomBusAnyDataListener busAnyDataListener;
    private BusDataStubListener busDataStubListener;
    private BusDataStubExtensionListener busDataStubExtensionListener;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        busAnyDataListener = CustomBusAnyDataListener.getNewBusAnyDataListener();
        busDataStubListener = BusDataStubListener.getNewBusDataStubListener();
        busDataStubExtensionListener = BusDataStubExtensionListener.getNewBusDataStubExtensionListener();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        busAnyDataListener = null;
        busDataStubListener = null;
        busDataStubExtensionListener = null;
    }

    public void testGetExpectedDataClass() throws Exception {
        assertEquals("Class should be null (for any data)", null, busAnyDataListener.getExpectedDataClass());
        assertEquals("Class should be BusDataStub", BusDataStub.class, busDataStubListener.getExpectedDataClass());
        assertEquals("Class should be BusDataStubExtension", BusDataStubExtension.class, busDataStubExtensionListener.getExpectedDataClass());
    }

}