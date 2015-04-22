package com.bq.autobus;

import org.jetbrains.annotations.NotNull;

/**
 * Created by imartinez on 13/04/15.
 * Stub class that duplicates the calls to notifyEvent of listeners every time
 * an event fires notifyEvent.
 */
public class BusDataDuplicatorBusPreprocessor<T> implements BusListener.Preprocessor<T> {

    @Override
    public void notifyEvent(@NotNull BusListener<T> listener, @NotNull T busData) {
        // Calls notifyEvent twice.
        listener.notifyEvent(busData);
        listener.notifyEvent(busData);
    }
}
