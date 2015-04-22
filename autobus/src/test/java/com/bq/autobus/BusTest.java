package com.bq.autobus;

import junit.framework.TestCase;

public class BusTest extends TestCase {

    private final String TEST_CHANNEL = "TEST_CHANNEL";
    private Bus bus;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        bus = new Bus();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        bus = null;
    }

    public void testGetBusListeners() throws Exception {
        // Subscribe first listener to test channel
        bus.subscribe(TEST_CHANNEL, BusDataStubListener.getNewBusDataStubListener());
        // Assert bus listeners subscribed
        assertEquals("One listener should be subscribed",
                1, bus.getBusListeners(TEST_CHANNEL).size());

        // Subscribe second listener to test channel
        bus.subscribe(TEST_CHANNEL, BusDataStubListener.getNewBusDataStubListener());
        // Assert bus listeners subscribed
        assertEquals("Two listeners should be subscribed",
                2, bus.getBusListeners(TEST_CHANNEL).size());

        // Subscribe 2 more listeners to test channel
        bus.subscribe(TEST_CHANNEL, BusDataStubListener.getNewBusDataStubListener());
        bus.subscribe(TEST_CHANNEL, BusDataStubListener.getNewBusDataStubListener());

        // Assert
        assertEquals("Four listeners should be subscribed",
                4, bus.getBusListeners(TEST_CHANNEL).size());
    }

    public void testSubscribeAndEmitEvent() throws Exception {
        // Create bus data listener
        BusDataStubListener busDataStubListener = BusDataStubListener.getNewBusDataStubListener();

        // Subscribe listener to test channel
        bus.subscribe(TEST_CHANNEL, busDataStubListener);

        // Emit event
        bus.emitEvent(TEST_CHANNEL, new BusDataStub());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener.getReceivedBusDataCount());
    }

    public void testUnSubscribe() throws Exception {
        // Create bus data listener
        BusDataStubListener busDataStubListener = BusDataStubListener.getNewBusDataStubListener();

        // Subscribe listener to test channel
        bus.subscribe(TEST_CHANNEL, busDataStubListener);

        // Emit event
        bus.emitEvent(TEST_CHANNEL, new BusDataStub());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener.getReceivedBusDataCount());

        // Unsubscribe listener from test channel
        bus.unSubscribe(TEST_CHANNEL, busDataStubListener);

        // Emit event
        bus.emitEvent(TEST_CHANNEL, new BusDataStub());

        // Assert event not received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener.getReceivedBusDataCount());
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

        // Subscribe first listener to test channel
        bus.subscribe(TEST_CHANNEL, busDataStubListener1);

        // Emit event
        bus.emitPersistentEvent(TEST_CHANNEL, new BusDataStub());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener1.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener2.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener3.getReceivedBusDataCount());

        // Subscribe second and third listeners to test channel
        bus.subscribe(TEST_CHANNEL, busDataStubListener2);
        bus.subscribe(TEST_CHANNEL, busDataStubListener3);

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener1.getReceivedBusDataCount());
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener2.getReceivedBusDataCount());
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener3.getReceivedBusDataCount());
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

        // Subscribe listeners to test channel
        bus.subscribe(TEST_CHANNEL, busAnyDataListener);
        bus.subscribe(TEST_CHANNEL, busDataStubListener);

        // Emit event
        bus.emitEvent(TEST_CHANNEL);

        // Assert event received only by busAnyDataListener
        assertEquals("Only one BusData should be delivered.",
                1, busAnyDataListener.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener.getReceivedBusDataCount());
    }

    /**
     * Tests that when a persistent event is emitted without data, all new subscribers expecting BusAnyData
     * are notified as soon as they subscribe.
     *
     * @throws Exception
     */
    public void testEmitPersistentEventWithoutData() throws Exception {
        // Create bus data listeners: one expecting any data and the other expecting BusDatStub data
        CustomBusAnyDataListener busAnyDataListener1 = CustomBusAnyDataListener.getNewBusAnyDataListener();
        BusDataStubListener busDataStubListener1 = BusDataStubListener.getNewBusDataStubListener();
        CustomBusAnyDataListener busAnyDataListener2 = CustomBusAnyDataListener.getNewBusAnyDataListener();
        BusDataStubListener busDataStubListener2 = BusDataStubListener.getNewBusDataStubListener();

        // Subscribe listeners to test channel
        bus.subscribe(TEST_CHANNEL, busAnyDataListener1);
        bus.subscribe(TEST_CHANNEL, busDataStubListener1);

        // Emit event without data
        bus.emitPersistentEvent(TEST_CHANNEL);

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busAnyDataListener1.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener1.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busAnyDataListener2.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubListener2.getReceivedBusDataCount());

        // Subscribe second and third listeners to test channel
        bus.subscribe(TEST_CHANNEL, busAnyDataListener2);
        bus.subscribe(TEST_CHANNEL, busDataStubListener2);

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

    /**
     * Tests that a listener expecting BusAnyData receives any kind of BusData.
     *
     * @throws Exception
     */
    public void testSubscribeForAnyData() throws Exception {
        // Create bus data listeners expecting any data.
        CustomBusAnyDataListener busAnyDataListener = CustomBusAnyDataListener.getNewBusAnyDataListener();

        // Subscribe listener to test channel
        bus.subscribe(TEST_CHANNEL, busAnyDataListener);

        // Emit events of any type
        bus.emitEvent(TEST_CHANNEL, new BusDataStub());
        bus.emitEvent(TEST_CHANNEL, "");
        bus.emitEvent(TEST_CHANNEL, 0);
        bus.emitEvent(TEST_CHANNEL, .0f);
        bus.emitEvent(TEST_CHANNEL, 0L);
        bus.emitEvent(TEST_CHANNEL, null);

        // Assert events received
        assertEquals("Six events should be delivered.",
                6, busAnyDataListener.getReceivedBusDataCount());
    }

    public void testSeveralChannels() throws Exception {
        String TEST_CHANNEL1 = "TEST_CHANNEL1";
        String TEST_CHANNEL2 = "TEST_CHANNEL2";

        // Create bus data listeners
        BusDataStubListener busDataStubListener1 = BusDataStubListener.getNewBusDataStubListener();
        BusDataStubListener busDataStubListener2 = BusDataStubListener.getNewBusDataStubListener();

        // Subscribe listener 1 to test channels 1 and 2.
        bus.subscribe(TEST_CHANNEL1, busDataStubListener1);
        bus.subscribe(TEST_CHANNEL2, busDataStubListener1);

        // Assert bus listeners subscribed
        assertEquals("One listener should be subscribed",
                1, bus.getBusListeners(TEST_CHANNEL1).size());
        assertEquals("One listener should be subscribed",
                1, bus.getBusListeners(TEST_CHANNEL2).size());

        // Subscribe listener 2 to test channels 1.
        bus.subscribe(TEST_CHANNEL1, busDataStubListener2);

        // Assert bus listeners subscribed
        assertEquals("Two listeners should be subscribed",
                2, bus.getBusListeners(TEST_CHANNEL1).size());
        assertEquals("No listeners should be subscribed",
                1, bus.getBusListeners(TEST_CHANNEL2).size());

        // Emit event on channel 1
        bus.emitEvent(TEST_CHANNEL1, new BusDataStub());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener1.getReceivedBusDataCount());
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener2.getReceivedBusDataCount());

        // Emit event on channel 2
        bus.emitEvent(TEST_CHANNEL2, new BusDataStub());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                2, busDataStubListener1.getReceivedBusDataCount());
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener2.getReceivedBusDataCount());
    }

    public void testSubscribeSameListenerTwiceFails() throws Exception {
        // Create bus data listeners expecting any data.
        CustomBusAnyDataListener busAnyDataListener = CustomBusAnyDataListener.getNewBusAnyDataListener();

        // Subscribe listener to test channel
        bus.subscribe(TEST_CHANNEL, busAnyDataListener);

        try {
            // Try to subscribe same listener to same channel again
            bus.subscribe(TEST_CHANNEL, busAnyDataListener);
            fail("Illegally subscribed an already subscribed listener.");
        } catch (IllegalArgumentException e) {
            assertEquals("Listener already subscribed to channel: " + TEST_CHANNEL, e.getMessage());
        }
    }

    public void testUnSubscribeNonSubscribedListenerFails() throws Exception {
        // Create bus data listeners expecting any data.
        CustomBusAnyDataListener busAnyDataListener = CustomBusAnyDataListener.getNewBusAnyDataListener();

        // Try to unsubscribe non-subscribed listener from test channel
        try {
            bus.unSubscribe(TEST_CHANNEL, busAnyDataListener);
            fail("Illegally unsubscribed non-subscribed listener.");
        } catch (IllegalArgumentException e) {
            assertEquals("Trying to unsubscribe non-subscribed listener from channel: " + TEST_CHANNEL, e.getMessage());
        }
    }

    /**
     * Tests that listeners expecting a class of data (BusDataStubExtension) derived from a base class
     * (BusDataStub) are not notified when an event containing the base class data is emitted on the Bus.
     *
     * @throws Exception
     */
    public void testBusDataAreNotDeliveredToListenersExpectingBusDataDerived() throws Exception {
        // Create bus data listeners
        BusDataStubListener busDataStubListener = BusDataStubListener.getNewBusDataStubListener();
        BusDataStubExtensionListener busDataStubExtensionListener = BusDataStubExtensionListener.getNewBusDataStubExtensionListener();

        // Subscribe listener to test channel
        bus.subscribe(TEST_CHANNEL, busDataStubListener);
        bus.subscribe(TEST_CHANNEL, busDataStubExtensionListener);

        // Emit event of class BusDataStub
        bus.emitEvent(TEST_CHANNEL, new BusDataStub());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener.getReceivedBusDataCount());
        assertEquals("No BusData should be delivered.",
                0, busDataStubExtensionListener.getReceivedBusDataCount());

        // Emit event of class BusDataStubExtension
        bus.emitEvent(TEST_CHANNEL, new BusDataStubExtension());

        // Assert event received
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubListener.getReceivedBusDataCount());
        assertEquals("Only one BusData should be delivered.",
                1, busDataStubExtensionListener.getReceivedBusDataCount());
    }

    public void testEnableAndDisableLogs() throws Exception {
        // Disable logs
        bus.setLoggingEnabled(false);
        assertEquals("Logging should be disabled.", false, bus.isLoggingEnabled());

        // Enable logs
        bus.setLoggingEnabled(true);
        assertEquals("Logging should be enabled.", true, bus.isLoggingEnabled());
    }

    public void testBusListenerWithPreprocessor() throws Exception {
        // Create bus data with a preprocessor that emits events twice.
        BusDataStubListenerWithPreprocessor busDataStubListener = BusDataStubListenerWithPreprocessor.getNewBusDataStubListener();

        // Subscribe listener to test channel
        bus.subscribe(TEST_CHANNEL, busDataStubListener);

        // Emit event
        bus.emitEvent(TEST_CHANNEL, new BusDataStub());

        // Assert event received
        assertEquals("Two BusData should be delivered.",
                2, busDataStubListener.getReceivedBusDataCount());
    }

    public void testEmitToChannelWithoutSubscriptors() throws Exception {
        // Emit event of class BusDataStub
        bus.emitEvent(TEST_CHANNEL, new BusDataStub());

        // Check if crashed
        assertTrue("Should not have crashed", true);
    }

    public void testBusSubscriptionWithInvalidParameters() {
        // Null channel (should crash)
        try {
            bus.subscribe(null, CustomBusAnyDataListener.getNewBusAnyDataListener());
        } catch (IllegalArgumentException e) {
            assertEquals("Channel must not be null", e.getMessage());
        }

        // Null listener (should crash)
        try {
            bus.subscribe(TEST_CHANNEL, null);
        } catch (IllegalArgumentException e) {
            assertEquals("Listener must not be null", e.getMessage());
        }

        // Null channel and listener
        try {
            bus.subscribe(null, null);
        } catch (IllegalArgumentException e) {
            assertEquals("Listener must not be null", e.getMessage());
        }
    }

    public void testBusUnsubscriptionWithInvalidParameters() {
        // Null channel (should crash)
        try {
            bus.unSubscribe(null, CustomBusAnyDataListener.getNewBusAnyDataListener());
        } catch (IllegalArgumentException e) {
            assertEquals("Channel must not be null", e.getMessage());
        }

        // Null listener (should crash)
        try {
            bus.unSubscribe(TEST_CHANNEL, null);
        } catch (IllegalArgumentException e) {
            assertEquals("Listener must not be null", e.getMessage());
        }

        // Null channel and listener
        try {
            bus.unSubscribe(null, null);
        } catch (IllegalArgumentException e) {
            assertEquals("Listener must not be null", e.getMessage());
        }
    }
}