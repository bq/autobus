 __Autobus__ <br/>An event bus by __BQ__
======================================

![alt text](https://raw.githubusercontent.com/bq/autobus/master/autobus_logo.png "Autobus logo")

What?
------------------------------
An enhanced event bus intended to allow communication between decoupled parts of an application.

It is based on concepts from [Otto][1], [EventBus][2] and [RxJava][3].

**For usage instructions please see [Autobus website][4].**


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


Download
--------

Autobus is available on Maven Central. Please ensure that you are using the latest version by checking [here][5]. 

**Maven**:

```xml
<dependency>
  <groupId>com.bq</groupId>
  <artifactId>autobus</artifactId>
  <version>0.1.0</version>
</dependency>
```
or **Gradle**:
```groovy
compile 'com.bq:autobus:0.1.0'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

License
-------
This project is licensed under the Apache Software License, Version 2.0.

    Copyright (c) 2015 BQ

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
 [4]: http://opensource.bq.com/autobus/
 [5]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bq%22%20AND%20a%3A%22autobus%22
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/