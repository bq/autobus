/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bq.autobus.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Random;

import static android.view.View.OnClickListener;

public class LocationActivity extends FragmentActivity {
    public static final float DEFAULT_LAT = 40.440866f;
    private static float lastLatitude = DEFAULT_LAT;
    public static final float DEFAULT_LON = -79.994085f;
    private static float lastLongitude = DEFAULT_LON;
    private static final float OFFSET = 0.1f;
    private static final Random RANDOM = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_history);

        findViewById(R.id.clear_location).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tell everyone to clear their location history.
                BusProvider.getInstance().emitEvent(BusChannels.LOCATION_CLEAR_BUS_CHANNEL);

                // Post new location event for the default location.
                lastLatitude = DEFAULT_LAT;
                lastLongitude = DEFAULT_LON;
                BusProvider.getInstance().emitPersistentEvent(BusChannels.LOCATION_CHANGED_BUS_CHANNEL, produceLocationData());
            }
        });

        findViewById(R.id.move_location).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lastLatitude += (RANDOM.nextFloat() * OFFSET * 2) - OFFSET;
                lastLongitude += (RANDOM.nextFloat() * OFFSET * 2) - OFFSET;
                BusProvider.getInstance().emitPersistentEvent(BusChannels.LOCATION_CHANGED_BUS_CHANNEL, produceLocationData());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location_activity, menu);
        menu.findItem(R.id.action_enable_logs).setChecked(BusProvider.getInstance().isLoggingEnabled());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Enable or disable logs for the default bus.
        if (item.getItemId() == R.id.action_enable_logs) {
            boolean loggingEnabled = !BusProvider.getInstance().isLoggingEnabled();
            BusProvider.getInstance().setLoggingEnabled(loggingEnabled);
            item.setChecked(loggingEnabled);
        }
        return super.onOptionsItemSelected(item);
    }

    public LocationChangedBusData produceLocationData() {
        // Provide an initial value for location based on the last known position.
        return new LocationChangedBusData(lastLatitude, lastLongitude);
    }
}