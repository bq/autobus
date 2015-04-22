package com.bq.autobus.sample;

import com.bq.autobus.Bus;

/**
 * Maintains a singleton instance for obtaining the bus (Notifier). Ideally this would be replaced with a more efficient means
 * such as through injection directly into interested classes.
 */
public class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }

}
