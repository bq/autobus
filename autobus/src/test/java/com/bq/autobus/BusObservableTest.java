package com.bq.autobus;

import junit.framework.TestCase;

public class BusObservableTest extends TestCase {

    private static final String TEST_CHANNEL = "TEST_CHANNEL";
    private Bus bus;
    private BusObservable<BusDataStub> busObservableOfDataSub;
    private BusObservable busObservableOfAnyData;

    public void setUp() throws Exception {
        super.setUp();
        bus = new Bus();
        busObservableOfDataSub = new BusObservable<>(TEST_CHANNEL, bus);
        busObservableOfAnyData = new BusObservable(TEST_CHANNEL, bus);
    }

    public void tearDown() throws Exception {
        bus = null;
        busObservableOfDataSub = null;
        busObservableOfAnyData = null;
    }

    public void testInstantiationWithInvalidParameters() {
        // Null channel (should crash)
        try {
            new BusObservable<>(null, bus);
        } catch (IllegalArgumentException e) {
            assertEquals("Channel must not be null", e.getMessage());
        }

        // Null listener (should crash)
        try {
            new BusObservable<>(TEST_CHANNEL, null);
        } catch (IllegalArgumentException e) {
            assertEquals("Bus must not be null", e.getMessage());
        }

        // Null channel and listener (should crash)
        try {
            new BusObservable<>(null, null);
        } catch (IllegalArgumentException e) {
            assertEquals("Channel must not be null", e.getMessage());
        }
    }

    public void testSubscribeWithInvalidListener() {
        try {
            busObservableOfAnyData.subscribe(null);
        } catch (IllegalArgumentException e) {
            assertEquals("Listener must not be null", e.getMessage());
        }
    }

    public void testUnsubscribeWithInvalidListener() {
        try {
            busObservableOfAnyData.unSubscribe(null);
        } catch (IllegalArgumentException e) {
            assertEquals("Listener must not be null", e.getMessage());
        }
    }

    public void testSubscribeAndEmitEvent() throws Exception {
        // Create bus data listener
        BusDataStubListener busDataStubListener = BusDataStubListener.getNewBusDataStubListener();

        // Subscribe listener using observable
        busObservableOfDataSub.subscribe(busDataStubListener);

        // Emit event using observable
        busObservableOfDataSub.emitEvent(new BusDataStub());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener.getReceivedBusDataCount());
    }

    public void testUnSubscribe() throws Exception {
        // Create bus data listener
        BusDataStubListener busDataStubListener = BusDataStubListener.getNewBusDataStubListener();

        // Subscribe listener using observable
        busObservableOfDataSub.subscribe(busDataStubListener);

        // Emit event using observable
        busObservableOfDataSub.emitEvent(new BusDataStub());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener.getReceivedBusDataCount());

        // Unsubscribe listener using observable
        busObservableOfDataSub.unSubscribe(busDataStubListener);

        // Emit event using observable
        busObservableOfDataSub.emitEvent(new BusDataStub());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener.getReceivedBusDataCount());
    }

    /**
     * Tests that when a event is emitted without data, only listeners expecting BusAnyData
     * receive the event.
     *
     * @throws Exception
     */
    public void testEmitEventWithoutData() throws Exception {
        // Create bus data listeners: one expecting any data and the other expecting BusDatStub data
        CustomBusAnyDataListener busAnyDataListener = CustomBusAnyDataListener.getNewBusAnyDataListener();
        BusDataStubListener busDataStubListener = BusDataStubListener.getNewBusDataStubListener();

        // Subscribe listeners using observables
        busObservableOfAnyData.subscribe(busAnyDataListener);
        busObservableOfAnyData.subscribe(busDataStubListener);

        // Emit event using observable
        busObservableOfAnyData.emitEvent();

        // Assert event received only by busAnyDataListener
        assertEquals("Only one BusData should be delivered.",
                1, busAnyDataListener.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener.getReceivedBusDataCount());
    }

    /**
     * Tests that when a persistent event is emitted, all new subscribers are notified
     * as soon as they subscribe.
     *
     * @throws Exception
     */
    public void testEmitPersistentEvent() throws Exception {
        // Create bus data listener
        BusDataStubListener busDataStubListener1 = BusDataStubListener.getNewBusDataStubListener();
        BusDataStubListener busDataStubListener2 = BusDataStubListener.getNewBusDataStubListener();
        BusDataStubListener busDataStubListener3 = BusDataStubListener.getNewBusDataStubListener();

        // Subscribe first listener to observable
        busObservableOfDataSub.subscribe(busDataStubListener1);

        // Emit event
        busObservableOfDataSub.emitPersistentEvent(new BusDataStub());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener1.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener2.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener3.getReceivedBusDataCount());

        // Subscribe second and third listeners to observable
        busObservableOfDataSub.subscribe(busDataStubListener2);
        busObservableOfDataSub.subscribe(busDataStubListener3);

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener1.getReceivedBusDataCount());
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener2.getReceivedBusDataCount());
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener3.getReceivedBusDataCount());
    }

    public void testEmitPersistentEventWithoutData() throws Exception {
        // Create bus data listeners: one expecting any data and the other expecting BusDatStub data
        CustomBusAnyDataListener busAnyDataListener1 = CustomBusAnyDataListener.getNewBusAnyDataListener();
        BusDataStubListener busDataStubListener1 = BusDataStubListener.getNewBusDataStubListener();
        CustomBusAnyDataListener busAnyDataListener2 = CustomBusAnyDataListener.getNewBusAnyDataListener();
        BusDataStubListener busDataStubListener2 = BusDataStubListener.getNewBusDataStubListener();

        // Subscribe listeners to observable
        busObservableOfAnyData.subscribe(busAnyDataListener1);
        busObservableOfAnyData.subscribe(busDataStubListener1);

        // Emit event without data
        busObservableOfAnyData.emitPersistentEvent();

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busAnyDataListener1.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener1.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busAnyDataListener2.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener2.getReceivedBusDataCount());

        // Subscribe second and third listeners to observable
        busObservableOfAnyData.subscribe(busAnyDataListener2);
        busObservableOfAnyData.subscribe(busDataStubListener2);

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busAnyDataListener1.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener1.getReceivedBusDataCount());
        // Sticky event should be delivered to this recently subscribed listener
        assertEquals("Only one BusData should be delivered.",
                1, busAnyDataListener2.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener2.getReceivedBusDataCount());
    }
}