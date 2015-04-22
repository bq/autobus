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
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;

import com.bq.autobus.BusAnyDataListener;
import com.bq.autobus.BusListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintain a scrollable history of location events.
 */
public class LocationHistoryFragment extends ListFragment {
    private final List<String> locationEvents = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private BusListener<LocationChangedBusData> locationChangedEventListener = new BusListener<LocationChangedBusData>(LocationChangedBusData.class) {
        @Override
        public void notifyEvent(@NotNull LocationChangedBusData busData) {
            onLocationChanged(busData);
        }
    };

    // Mejorar el API cuando no se envían/reciben datos con el evento.
    private BusListener locationClearEventListener = new BusAnyDataListener() {
        @Override
        public void notifyEvent(@Nullable Object busData) {
            onLocationCleared();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        // Subscribe to events
        BusProvider.getInstance().subscribe(BusChannels.LOCATION_CHANGED_BUS_CHANNEL, locationChangedEventListener);
        BusProvider.getInstance().subscribe(BusChannels.LOCATION_CLEAR_BUS_CHANNEL, locationClearEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unsubscribe from events
        BusProvider.getInstance().unSubscribe(BusChannels.LOCATION_CHANGED_BUS_CHANNEL, locationChangedEventListener);
        BusProvider.getInstance().unSubscribe(BusChannels.LOCATION_CLEAR_BUS_CHANNEL, locationClearEventListener);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, locationEvents);
        setListAdapter(adapter);
    }

    public void onLocationChanged(LocationChangedBusData event) {
        locationEvents.add(0, event.toString());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void onLocationCleared() {
        locationEvents.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
