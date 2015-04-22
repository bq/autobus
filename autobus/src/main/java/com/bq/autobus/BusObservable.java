package com.bq.autobus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an observable object defined by a Bus and a concrete channel.
 * It enables an easy and well defined way of subscribing to and emitting events containing
 * a concrete class of data.
 * <p/>
 * Created by imartinez on 07/04/15.
 */
public class BusObservable<T> {

    private final String channel;
    private final Bus bus;

    /**
     * Creates a new BusObservable with the given channel and Bus.
     *
     * @param channel String representing the channel the event will be emitted to and the
     *                subscribers will be subscribed to.
     * @param bus     Bus where the emission and subscription will take place.
     * @throws IllegalArgumentException if the channel or the bus are null.
     */
    public BusObservable(@NotNull String channel, @NotNull Bus bus) {
        if (channel == null) throw new IllegalArgumentException("Channel must not be null");
        this.channel = channel;

        if (bus == null) throw new IllegalArgumentException("Bus must not be null");
        this.bus = bus;
    }

    /**
     * Subscribe a BusListener to the channel.
     *
     * @param listener BusListener to notify when an event containing data of the class expected by the listener
     *                 is emitted on the channel.
     * @throws IllegalArgumentException if the listener is null or if is already subscribed to the channel.
     */
    public void subscribe(@NotNull BusListener<T> listener) {
        bus.subscribe(channel, listener);
    }

    /**
     * Unsubscribe a BusListener from the channel.
     *
     * @param listener BusListener being unsubscribed from the channel.
     * @throws IllegalArgumentException if the listener is null or if is not subscribed to the channel.
     */
    public void unSubscribe(@NotNull BusListener<T> listener) {
        bus.unSubscribe(channel, listener);
    }

    /**
     * Emit event to the channel without data.
     * Only BusListeners subscribed to the channel and expecting the data class will be notified.
     */
    public void emitEvent() {
        bus.emitEvent(channel);
    }

    /**
     * Emit event to the channel containing data.
     * Only BusListeners subscribed to the channel and expecting the data class will be notified.
     *
     * @param busData data sent to the channel.
     */
    public void emitEvent(@Nullable T busData) {
        bus.emitEvent(channel, busData);
    }

    /**
     * Emit persistent event to the channel without data.
     * Persistent events are notified to new matching subscribers as soon as they subscribe to the channel.
     * Only BusListeners subscribed to the channel and expecting the data class will be notified.
     */
    public void emitPersistentEvent() {
        bus.emitPersistentEvent(channel);
    }

    /**
     * Emit persistent event to the channel containing data.
     * Persistent events are notified to new matching subscribers as soon as they subscribe to the channel.
     * Only BusListeners subscribed to the channel and expecting the data class will be notified.
     *
     * @param busData data sent to the channel.
     */
    public void emitPersistentEvent(@Nullable T busData) {
        bus.emitPersistentEvent(channel, busData);
    }

}
