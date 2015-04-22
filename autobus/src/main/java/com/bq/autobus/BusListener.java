package com.bq.autobus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a listener that can be subscribed to a Bus channel expecting a concrete
 * class of object.
 * <p/>
 * Created by imartinez on 10/04/15.
 */
public abstract class BusListener<T> {

    private final Class<T> expectedDataClass;
    private Preprocessor<T> preprocessor;

    /**
     * Creates a BusListener that expects a concrete class.
     *
     * @param expectedDataClass Class of the expected data. Only events containing objects of this class
     *                          will be notified to this listener. If you want to be notified
     *                          with any class of data sent to a Bus channel should use BusAnyDataListener.
     */
    public BusListener(@NotNull Class<T> expectedDataClass) {
        this(expectedDataClass, null);
    }

    /**
     * Creates a BusListener that expects a concrete class and a Preprocessor.
     *
     * @param expectedDataClass Class of the expected data. Only events containing objects of this class
     *                          will be notified to this listener. If you want to be notified
     *                          with any class of data sent to a Bus channel should use BusAnyDataListener.
     * @param preprocessor      Set a preprocessor to intercept every event notification.
     *                          Useful, for example, when you need to execute listeners on the main thread.
     */
    public BusListener(@NotNull Class<T> expectedDataClass, @Nullable Preprocessor<T> preprocessor) {
        this.expectedDataClass = expectedDataClass;
        this.preprocessor = preprocessor;
    }

    /**
     * Retrieves the expected data class set in the constructor.
     *
     * @return Class the expected data class.
     */
    @NotNull
    /*package*/ Class<T> getExpectedDataClass() {
        return expectedDataClass;
    }

    /**
     * Retrieves the preprocessor.
     *
     * @return The preprocessor or null if is not set.
     */
    @Nullable
    /*package*/ Preprocessor<T> getPreprocessor() {
        return preprocessor;
    }

    /**
     * @return true if Preprocessor is set, false otherwise.
     */
    public boolean hasPreprocessor() {
        return preprocessor != null;
    }

    /**
     * Callback executed when an event with the expected data class is emitted to the bus channel this
     * listener is subscribed to.
     *
     * @param busData data object.
     */
    public abstract void notifyEvent(@NotNull T busData);

    /**
     * Preprocessor to intercept every event notification.
     * Useful, for example, when you need to execute listeners on the main thread.
     */
    public interface Preprocessor<T> {
        void notifyEvent(@NotNull BusListener<T> listener, @NotNull T busData);
    }
}
