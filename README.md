 __Autobus__ <br/>An event bus by __bq__
======================================

What?
------------------------------
An enhanced event bus intended to allow communication between decoupled parts of an application.

It is based on concepts from [Otto][1], [EventBus][2] and [RxJava][3].


Why?
------------------------------
We developed our own Event Bus to cover two main needs that aren't covered by existing libraries:

* __Decouple channels and emitted data__

Both Otto and EventBus use Events (defined classes) to carry data and also to represent the channel the subscribers
are listening to. This approach is valid but it is quite inflexible. 

Autobus allows representing channels as Strings and
data as a separate class; this way a subscriber can listen to a channel expecting a concrete class of data (or even Any class
of data) and emitters can send data of a concrete class (or no data at all) to a concrete channel. When an emitter sends data of
class D to a channel C, every subscriber listening to channel C and expecting data of class D is notified. Also subscribers
listening to channel C and expecting data of any class are notified.

* __Explicit contract between subscribers and emitters__

As one of the common use case of Event Buses is the communication between _interactors_ (asynchronous core operations of an app) and _presenters_ (adapters between _views_ and _interactors_) it is desirable to maintain an explicit definition or contract between the emitters and the subscribers. Autobus accomplishes this task with BusObservable API. A _interactor_ can return a BusObservable instance and subscribers can directly be subscribed to this observable; the observable is the contract itself as it defines the _Bus_, the _Channel_ and also the data that will be emitted. 


Ok, what else?
------------------------------
* Full annotated with @Nullable and @NotNull.
* Clean code enforcement by strong typing. No need for upcasting/downcasting any object.
* Autocomplete heaven (related to this string typing).

Comparison
----------
| Functionality                   | EventBus        | Otto        | Autobus                           |
| --------------------------------|-----------------|-------------|---------------------------------- |
| Declare event handling methods  | Name Convention | Annotations | Explicity                         |
| Event inheritance               | Yes             | Yes         | No                                |
| Cache most recent events        | Yes (sticky)    | No          | Yes (persistent emmit)            |
| Event producers                 | No              | Yes         | No                                |
| Event delivery in posting thread| Yes             | Yes         | Yes                               |
| Event delivery in main thread   | Yes             | No          | Yes (using preprocessor)          |
| Event delivery in bg thread     | Yes             | No          | Yes                               |
| Aynchronous event delivery      | Yes             | No          | Yes                               |


Usage
------
We are working to make this lib available in a Maven repository.
For the moment you can find the latest stable JAR in the root of this GitHub repo as autobus-0.1.0.jar or
you can build the JAR file yourself from the source code.


Quick start
--------
You can use Autobus in two different ways:

## * __Using Bus API__
When you want direct access to Autobus basic API and are not worried about a strong contract.

* Define data (you could also use your own classes or even String, Integer...):

```java
public class LocationChangedBusData { /* Additional fields if needed */ }
```
  * Prepare listeners expecting a concrete class of data:

```java
private BusListener<LocationChangedBusData> locationChangedEventListener1 = new BusListener<LocationChangedBusData>(LocationChangedBusData.class) {
        @Override
        public void notifyEvent(@NotNull LocationChangedBusData busData) { // Notice the strong typed not-null parameter
            onLocationChanged(busData);
        }
    };
```

  * ... or any class of data at all (will be notified even if there is the data is null):

```java
private BusListener locationChangedEventListener2 = new BusAnyDataListener() {
        @Override
        public void notifyEvent(@Nullable Object busData) { // In this case there is no string typing, and the data could be null 
            onLocationChanged();
        }
    };
```

  * Subscribe listener to a bus (should be injected) and a channel, expecting a concrete class:

```java
bus.subscribe("TEST_CHANNEL", locationChangedEventListener1);
bus.subscribe("TEST_CHANNEL", locationChangedEventListener2);
```

  * Post events with data (both previously defined listeners will be notified):

```java
bus.emitEvent("TEST_CHANNEL", new LocationChangedBusData());
```

  * ... or post events without data (only locationChangedEventListener2 will be notified):

```java
bus.emitEvent("TEST_CHANNEL");
```


## * __Using BusObservable API__
When you prefer defining a strong contract between subscribers and emitters (leads to cleaner and safer code) use this approach.
  
* Define data (you could also use your own classes or even String, Integer...):

```java
public class LocationChangedBusData { /* Additional fields if needed */ }
```

  * Define BusObservable passing a channel and a bus (should be injected) and specifying the class that will be emitted through it. Only listeners expecting that class of data will be able to subscribe to this observable. And all the data emitted through this observable will be of the same class. Strong contract!

```java
BusObservable<LocationChangedBusData> busObservable = new BusObservable<>("TEST_CHANNEL", bus);
```

  * ... you could also define a BusObservable without specifying a concrete class of data:

```java
BusObservable busObservable = new BusObservable("TEST_CHANNEL", bus);
```

  * Prepare listeners expecting a concrete class of data:

```java
private BusListener<LocationChangedBusData> locationChangedEventListener1 = new BusListener<LocationChangedBusData>(LocationChangedBusData.class) {
        @Override
        public void notifyEvent(@NotNull LocationChangedBusData busData) { // Notice the strong typed not-null parameter
            onLocationChanged(busData);
        }
    };
```

  * Subscribe listener to the BusObservable. Only listeners expecting *LocationChangedBusData* will be able to subscribe to this BusObservable:

```java
busObservable.subscribe(listener);
```

  * Post events with data using BusObservable. Only data of class *LocationChangedBusData* can be emitted using this BusObservable:

```java
busObservable.emitEvent(new BusDataSample());
```

## Need persistent events? We got you covered

* You can just emit an event or you can emit **persistent** events. Persistent events are notified to new matching subscribers as soon as they subscribe to the channel.

```java
bus.emitPersistentEvent("TEST_CHANNEL", new LocationChangedBusData());
```

## Need to execute your callback in the **main thread**? Here we go...

* You can define a Preprocessor for your listeners (both BusListener and BusAnyDataListener). This preprocessor will act as an interceptor that will receive every notification intended for the listener. You can build any kind of preprocessing of the data there or even force the listener's *notifyEvent* to be executed on the main thread:

```java
public class ForceMainThreadPreprocessor<T> implements BusListener.Preprocessor<T> {

    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void notifyEvent(final @NotNull BusListener<T> listener, final @NotNull T busData) {
        // Force listener's notifyEvent to be executed on the main thread.
        handler.post(new Runnable() {
            @Override public void run() {
                listener.notifyEvent(busData);
            }
        });
    }
}
```

Full documentation
------------------

## Class Bus

Event bus. 

* Supports different channels and events sent to each channel containing data of different classes or no data at all. BusListeners subscribed to a concrete channel and expecting a concrete class will be notified when a event containing a data object of that class is emitted to that channel.

* Supports persistent events that are notified to new matching subscribers as soon as they subscribe to the appropriate channel.

#### `Bus()`

Creates a new Bus instance.

#### `boolean isLoggingEnabled()`

Check whether logging is enabled for this bus. Enabled by default.

 * **Returns:** true if logging is enabled for this bus.

#### `void setLoggingEnabled(boolean loggingEnabled)`

Sets whether logging should be enabled for this bus. Enabled by default.

 * **Parameters:** `loggingEnabled` — 

#### `void subscribe(@NotNull String channel, @NotNull BusListener listener)`

Subscribe a BusListener to a concrete channel. 

 * **Parameters:**
   * `channel` — String representing the channel the BusListener is being subscribed to.
   * `listener` — BusListener to notify when an event containing data of the class expected by the listener is emitted on the channel.
 * **Exceptions:** `IllegalArgumentException` — if the listener is already subscribed to the channel or channel is null.

#### `void unSubscribe(@NotNull String channel, @NotNull BusListener listener)`

Unsubscribe a BusListener from a concrete channel.

 * **Parameters:**
   * `channel` — String representing the channel from which the listener is being unsubscribed.
   * `listener` — BusListener being unsubscribed from the channel.
 * **Exceptions:** `IllegalArgumentException` — if the listener is not subscribed to the channel or channel is null.

#### `void emitPersistentEvent(@NotNull String channel)`

Emit persistent event without data. Persistent events are notified to new matching subscribers as soon as they subscribe to the channel. Only BusListeners subscribed to the channel and expecting the data class will be notified.

 * **Parameters:** `channel` — String representing the channel the event will be emitted to.
 * **Exceptions:** `IllegalArgumentException` — if channel is null.

#### `void emitPersistentEvent(@NotNull String channel, @Nullable Object busData)`

Emit persistent event containing data. Persistent events are notified to new matching subscribers as soon as they subscribe to the channel. Only BusListeners subscribed to the channel and expecting the data class will be notified.

 * **Parameters:**
   * `channel` — String representing the channel the event will be emitted to.
   * `busData` — data sent to the channel.
 * **Exceptions:** `IllegalArgumentException` — if channel is null.

#### `void emitEvent(@NotNull String channel)`

Emit event without data. Only BusListeners subscribed to the channel and expecting the data class will be notified.

 * **Parameters:** `channel` — String representing the channel the event will be emitted to.
 * **Exceptions:** `IllegalArgumentException` — if channel is null.

#### `void emitEvent(@NotNull String channel, @Nullable Object busData)`

Emit event containing data. Only BusListeners subscribed to the channel and expecting the data class will be notified.

 * **Parameters:**
   * `channel` — String representing the channel the event will be emitted to.
   * `busData` — data sent to the channel.
 * **Exceptions:** `IllegalArgumentException` — if channel is null.

## Class BusObservable<T>

Represents an observable object defined by a Bus and a concrete channel. It enables an easy and well defined way of subscribing to and emitting events containing a concrete class of data.

#### `BusObservable(@NotNull String channel, @NotNull Bus bus)`

Creates a new BusObservable with the given channel and Bus. 

 * **Parameters:**
   * `channel` — String representing the channel the event will be emitted to and the subscribers will be subscribed to.
   * `bus` — Bus where the emission and subscription will take place.
 * **Exceptions:** `IllegalArgumentException` — if the channel or the bus are null.

#### `void subscribe(@NotNull BusListener<T> listener)`

Subscribe a BusListener to the channel. 

 * **Parameters:** `listener` — BusListener to notify when an event containing data of the class expected by the listener is emitted on the channel.
 * **Exceptions:** `IllegalArgumentException` — if the listener is null or if is already subscribed to the channel.

#### `void unSubscribe(@NotNull BusListener<T> listener)`

Unsubscribe a BusListener from the channel.

 * **Parameters:** `listener` — BusListener being unsubscribed from the channel.
 * **Exceptions:** `IllegalArgumentException` — if the listener is null or if is not subscribed to the channel.

#### `void emitEvent()`

Emit event to the channel without data. Only BusListeners subscribed to the channel and expecting the data class will be notified.

#### `void emitEvent(@Nullable T busData)`

Emit event to the channel containing data. Only BusListeners subscribed to the channel and expecting the data class will be notified.

 * **Parameters:** `busData` — data sent to the channel.

#### `void emitPersistentEvent()`

Emit persistent event to the channel without data. Persistent events are notified to new matching subscribers as soon as they subscribe to the channel. Only BusListeners subscribed to the channel and expecting the data class will be notified.

#### `void emitPersistentEvent(@Nullable T busData)`

Emit persistent event to the channel containing data. Persistent events are notified to new matching subscribers as soon as they subscribe to the channel. Only BusListeners subscribed to the channel and expecting the data class will be notified.

 * **Parameters:** `busData` — data sent to the channel.

## Class BusListener<T>

Represents a listener that can be subscribed to a Bus channel expecting a concrete class of object.

#### `public BusListener(@NotNull Class<T> expectedDataClass)`

Creates a BusListener that expects a concrete class. 

 * **Parameters:** `expectedDataClass` — Class of the expected data. Only events containing objects of this class will be notified to this listener. If you want to be notified with any class of data sent to a Bus channel should use BusAnyDataListener.

#### `public BusListener(@NotNull Class<T> expectedDataClass, @Nullable Preprocessor<T> preprocessor)`

Creates a BusListener that expects a concrete class and a Preprocessor. 

 * **Parameters:**
   * `expectedDataClass` — Class of the expected data. Only events containing objects of this class will be notified to this listener. If you want to be notified with any class of data sent to a Bus channel should use BusAnyDataListener. Useful, for example, when you need to execute listeners on the main thread.
   * `preprocessor` — Set a preprocessor to intercept every event notification.

#### `public boolean hasPreprocessor()`

 * **Returns:** true if Preprocessor is set, false otherwise.

#### `public abstract void notifyEvent(@NotNull T busData)`

Callback executed when an event with the expected data class is emitted to the bus channel this listener is subscribed to.

 * **Parameters:** `busData` — data object.

## interface BusListener.Preprocessor<T>
#### `public interface Preprocessor<T>`

Preprocessor to intercept every event notification. Useful, for example, when you need to execute listeners on the main thread.

## class BusAnyDataListener

Represents a listener that can be subscribed to a Bus channel expecting any kind of object (null included).

#### `public BusAnyDataListener()`

Creates a BusListener that expects data of any kind of class (even null)

#### `public BusAnyDataListener(@Nullable Preprocessor preprocessor)`

Creates a BusListener that expects data of any kind of class (even null) with a Preprocessor. 

 * **Parameters:** `preprocessor` — Set a preprocessor to intercept every event notification. Useful, for example, when you need to execute listeners on the main thread.

#### `public abstract void notifyEvent(@Nullable Object busData)`

Callback executed when an event with any kind of data (even null) is emitted to the bus channel this listener is subscribed to.

 * **Parameters:** `busData` — data object or null if no data was emitted.

## Class BusAnyDataListener.Preprocessor`

Preprocessor to intercept every event notification. Useful, for example, when you need to execute listeners on the main thread.


License
-------
This project is licensed under the Apache Software License, Version 2.0.

    Copyright (c) 2015 bq

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [1]: http://square.github.com/otto/
 [2]: https://github.com/greenrobot/EventBus
 [3]: https://github.com/ReactiveX/RxJava