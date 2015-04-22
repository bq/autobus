package com.bq.autobus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Event bus.
 * - Supports different channels and events sent to each channel containing data of different classes or
 * no data at all. BusListeners subscribed to a concrete channel and expecting a concrete class
 * will be notified when a event containing a data object of that class is emitted to that channel.
 * - Supports persistent events that are notified to new matching subscribers as soon as they subscribe
 * to the appropriate channel.
 * <p/>
 * Created by imartinez on 13/04/15.
 */
public final class Bus {

    private final HashMap<String, Object> historic;
    private final HashMap<String, List<BusListener>> observers;

    private Logger logger = Logger.getLogger("Autobus");

    /**
     * Creates a new Bus instance.
     */
    public Bus() {
        historic = new HashMap<>();
        observers = new HashMap<>();
    }

    /**
     * Check whether logging is enabled for this bus.
     * Enabled by default.
     *
     * @return true if logging is enabled for this bus.
     */
    public boolean isLoggingEnabled() {
        return logger != null && logger.getLevel() != Level.OFF;
    }

    /**
     * Sets whether logging should be enabled for this bus.
     * Enabled by default.
     *
     * @param loggingEnabled
     */
    public void setLoggingEnabled(boolean loggingEnabled) {
        logger.setLevel(loggingEnabled ? Level.INFO : Level.OFF);
    }

    /**
     * Subscribe a BusListener to a concrete channel.
     *
     * @param channel  String representing the channel the BusListener is being subscribed to.
     * @param listener BusListener to notify when an event containing data of the class expected by the listener
     *                 is emitted on the channel.
     * @throws IllegalArgumentException if the listener is already subscribed to the channel or channel is null.
     */
    public void subscribe(@NotNull String channel, @NotNull BusListener listener) {
        if (listener == null) throw new IllegalArgumentException("Listener must not be null");
        List<BusListener> busListeners = getBusListeners(channel);
        synchronized (busListeners) {
            // Check if this listener was already subscribed to this channel
            if (busListeners.contains(listener)) {
                throw new IllegalArgumentException("Listener already subscribed to channel: " + channel);
            }

            busListeners.add(listener);
            logger.info("BUS -> Listener subscribed to channel: " + channel + " for "
                    + ((listener instanceof BusAnyDataListener)
                    ? "any data." : "data: " + listener.getExpectedDataClass().toString()));
            if (historic.containsKey(channel)) {
                emit(historic.get(channel), Collections.singletonList(listener));
            }
        }
    }

    /**
     * Unsubscribe a BusListener from a concrete channel.
     *
     * @param channel  String representing the channel from which the listener is being unsubscribed.
     * @param listener BusListener being unsubscribed from the channel.
     * @throws IllegalArgumentException if the listener is not subscribed to the channel or channel is null.
     */
    public void unSubscribe(@NotNull String channel, @NotNull BusListener listener) {
        if (listener == null) throw new IllegalArgumentException("Listener must not be null");
        List<BusListener> busListeners = getBusListeners(channel);
        synchronized (busListeners) {
            // Check if this listener is subscribed to this channel
            if (!busListeners.contains(listener)) {
                throw new IllegalArgumentException("Trying to unsubscribe non-subscribed listener from channel: " + channel);
            }

            logger.info("BUS -> Listener Unsubscribed from channel: " + channel + ". Expected "
                    + ((listener instanceof BusAnyDataListener)
                    ? "any data." : "data: " + listener.getExpectedDataClass().toString()));
            busListeners.remove(listener);
        }
    }

    /**
     * Emit persistent event without data.
     * Persistent events are notified to new matching subscribers as soon as they subscribe to the channel.
     * Only BusListeners subscribed to the channel and expecting the data class will be notified.
     *
     * @param channel String representing the channel the event will be emitted to.
     * @throws IllegalArgumentException if channel is null.
     */
    public void emitPersistentEvent(@NotNull String channel) {
        emit(channel, null, true);
    }

    /**
     * Emit persistent event containing data.
     * Persistent events are notified to new matching subscribers as soon as they subscribe to the channel.
     * Only BusListeners subscribed to the channel and expecting the data class will be notified.
     *
     * @param channel String representing the channel the event will be emitted to.
     * @param busData data sent to the channel.
     * @throws IllegalArgumentException if channel is null.
     */
    public void emitPersistentEvent(@NotNull String channel, @Nullable Object busData) {
        emit(channel, busData, true);
    }

    /**
     * Emit event without data.
     * Only BusListeners subscribed to the channel and expecting the data class will be notified.
     *
     * @param channel String representing the channel the event will be emitted to.
     * @throws IllegalArgumentException if channel is null.
     */
    public void emitEvent(@NotNull String channel) {
        emit(channel, null, false);
    }

    /**
     * Emit event containing data.
     * Only BusListeners subscribed to the channel and expecting the data class will be notified.
     *
     * @param channel String representing the channel the event will be emitted to.
     * @param busData data sent to the channel.
     * @throws IllegalArgumentException if channel is null.
     */
    public void emitEvent(@NotNull String channel, @Nullable Object busData) {
        emit(channel, busData, false);
    }

    private void emit(@NotNull String channel, @Nullable Object eventData, boolean isPersistent) {
        List<BusListener> busListeners = getBusListeners(channel);
        logger.info("BUS -> Data of class: " + (eventData != null ? eventData.getClass().toString() : "null") + " emitted on channel: " + channel);
        synchronized (busListeners) {
            if (isPersistent) historic.put(channel, eventData);
            emit(eventData, busListeners);
        }
    }

    private void emit(@Nullable Object busData, @NotNull List<BusListener> listeners) {
        for (BusListener listener : listeners) {
            // If listener is a BusAnyDataListener data may be null.
            // Otherwise data must not be null.
            if (listener instanceof BusAnyDataListener ||
                    (busData != null && listener.getExpectedDataClass().equals(busData.getClass()))) {
                if (listener.hasPreprocessor()) {
                    listener.getPreprocessor().notifyEvent(listener, busData);

                    logger.info("BUS -> Notified listener's preprocessor expecting data of "
                            + ((listener instanceof BusAnyDataListener)
                            ? "any class" : "class: " + listener.getExpectedDataClass().toString()));
                } else {
                    listener.notifyEvent(busData);

                    logger.info("BUS -> Notified listener expecting data of "
                            + ((listener instanceof BusAnyDataListener)
                            ? "any class" : "class: " + listener.getExpectedDataClass().toString()));
                }
            } else {
                logger.info("BUS -> Did not notify listener on channel due to different data class expectation: Listener expected: " +
                        listener.getExpectedDataClass().toString() + " and data sent class was: " + (busData != null ? busData.getClass().toString() : "null"));
            }
        }
    }

    /**
     * Get all BusListener listeners subscribed to a concrete channel.
     *
     * @param channel String representing the channel being inspected.
     * @return List<BusListener> list of BusListeners subscribed to the channel.
     * @throws IllegalArgumentException if channel is null.
     */
    @NotNull
    /*package*/ List<BusListener> getBusListeners(@NotNull String channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }

        List<BusListener> busListeners;
        synchronized (observers) {
            busListeners = observers.get(channel);
            if (busListeners == null) {
                busListeners = new LinkedList<>();
                observers.put(channel, busListeners);
            }
        }
        return busListeners;
    }

}