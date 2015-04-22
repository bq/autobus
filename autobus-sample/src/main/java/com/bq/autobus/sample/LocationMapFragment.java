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

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bq.autobus.BusListener;
import com.bq.autobus.BusObservable;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

import static android.widget.ImageView.ScaleType.CENTER_INSIDE;

/**
 * Display a map centered on the last known location.
 */
public class LocationMapFragment extends Fragment {
    private static final String URL =
            "https://maps.googleapis.com/maps/api/staticmap?sensor=false&size=400x400&zoom=13&center=%s,%s";
    private static DownloadTask downloadTask;

    private ImageView imageView;

    private BusObservable<ImageAvailableBusData> imageAvailableEventDataBusObservable;

    private BusListener<LocationChangedBusData> locationChangedBusListener = new BusListener<LocationChangedBusData>(LocationChangedBusData.class) {
        @Override
        public void notifyEvent(@NotNull LocationChangedBusData busData) {
            onLocationChangedEvent(busData);
        }
    };

    private BusListener<ImageAvailableBusData> imageAvailableBusListener = new BusListener<ImageAvailableBusData>(ImageAvailableBusData.class) {
        @Override
        public void notifyEvent(@NotNull ImageAvailableBusData busData) {
            onImageAvailable(busData);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageAvailableEventDataBusObservable = new BusObservable<>(ImageAvailableBusData.IMAGE_AVAILABLE_EVENT_CHANNEL, BusProvider.getInstance());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Subscribe to events
        BusProvider.getInstance().subscribe(BusChannels.LOCATION_CHANGED_BUS_CHANNEL, locationChangedBusListener);
        imageAvailableEventDataBusObservable.subscribe(imageAvailableBusListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unsubscribe from events
        BusProvider.getInstance().unSubscribe(BusChannels.LOCATION_CHANGED_BUS_CHANNEL, locationChangedBusListener);
        imageAvailableEventDataBusObservable.unSubscribe(imageAvailableBusListener);

        // Stop existing download, if it exists.
        if (downloadTask != null) {
            downloadTask.cancel(true);
            downloadTask = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        imageView = new ImageView(getActivity());
        imageView.setScaleType(CENTER_INSIDE);
        return imageView;
    }

    public void onLocationChangedEvent(LocationChangedBusData locationChangedEvent) {
        // Stop existing download, if it exists.
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }

        // Trigger a background download of an image for the new location.
        downloadTask = new DownloadTask(imageAvailableEventDataBusObservable);
        downloadTask.execute(String.format(URL, locationChangedEvent.lat, locationChangedEvent.lon));
    }

    public void onImageAvailable(ImageAvailableBusData event) {
        if (imageView != null) {
            imageView.setImageDrawable(event.image);
        }
    }


    private static class ImageAvailableBusData {

        public final static String IMAGE_AVAILABLE_EVENT_CHANNEL = "IMAGE_AVAILABLE_EVENT_CHANNEL";

        public final Drawable image;

        ImageAvailableBusData(Drawable image) {
            this.image = image;
        }

    }

    private static class DownloadTask extends AsyncTask<String, Void, Drawable> {

        private BusObservable<ImageAvailableBusData> imageAvailableEventDataBusObservable;

        public DownloadTask(BusObservable<ImageAvailableBusData> imageAvailableEventDataBusObservable) {
            this.imageAvailableEventDataBusObservable = imageAvailableEventDataBusObservable;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            try {
                return BitmapDrawable.createFromStream(new URL(params[0]).openStream(), "bitmap.jpg");
            } catch (Exception e) {
                Log.e("LocationMapFragment", "Unable to download image.", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (!isCancelled() && drawable != null) {
                imageAvailableEventDataBusObservable.emitEvent(new ImageAvailableBusData(drawable));
            }
        }
    }
}
