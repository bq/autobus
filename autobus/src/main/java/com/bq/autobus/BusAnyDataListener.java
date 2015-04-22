package com.bq.autobus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a listener that can be subscribed to a Bus channel expecting
 * any kind of object (null included).
 * <p/>
 * Created by Izan Moreno on 17/04/2015.
 */
public abstract class BusAnyDataListener extends BusListener<Object> {
    /**
     * Creates a BusListener that expects data of any kind of class (even null)
     */
    public BusAnyDataListener() {
        this(null);
    }

    /**
     * Creates a BusListener that expects data of any kind of class (even null) with a Preprocessor.
     *
     * @param preprocessor Set a preprocessor to intercept every event notification.
     *                     Useful, for example, when you need to execute listeners on the main thread.
     */
    @SuppressWarnings(
            "ConstantConditions"
            // This listener expects data for any kind of type (even null),
            // so Class<?> is not required
    )
    public BusAnyDataListener(@Nullable Preprocessor preprocessor) {
        super(null, preprocessor);
    }

    /**
     * Callback executed when an event with any kind of data (even null) is emitted
     * to the bus channel this listener is subscribed to.
     *
     * @param busData data object or null if no data was emitted.
     */
    public abstract void notifyEvent(@Nullable Object busData);

    /**
     * Preprocessor to intercept every event notification.
     * Useful, for example, when you need to execute listeners on the main thread.
     */
    public abstract class Preprocessor implements BusListener.Preprocessor<Object> {
        @Override
        public final void notifyEvent(@NotNull BusListener<Object> listener, @NotNull Object busData) {
            // Listener is always an instance of BusAnyDataListener
            notifyEvent((BusAnyDataListener) listener, busData);
        }

        public abstract void notifyEvent(@NotNull BusAnyDataListener listener, @Nullable Object busData);
    }
}
