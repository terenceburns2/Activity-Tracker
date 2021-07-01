# Activity Tracker

Part of my Mobile Programming module at university, I was tasked with creating an Activity Tracker/Running application. The main aim of this project was to implement standard features of a usual activity tracker, but to also design it with good architecture, encouraging a complete separation of concerns between the UI and business logic. There was also a strong focus on efficiency, therefore, offloading work on the main UI thread was a must.

## Learning objectives ✔️

+ Allow the user to:
  + Record the duration of their activity.
  + Record the speed of their activity.
  + Record the distance of their activity.
+ Main a seperation of concerns through the use of MVVM architecture.
+ Ensure efficiency through the use of worker threads.
+ Structure architecture appropriate for seamless interaction between each defined Android Components.


## Functionality 🔋

Overall, the application allows all of the standard features expected from an activity tracker, including, recording duration, distance, and speed of the activity. The user can also review their previous activities in a feed fragment. The previous activities are saved locally via an SQLite database and fetched appropriately within a RecyclerView, this improves performance by continually recycling views when necessary, rather than destroying them when they go out of view. In addition, the use of lifecycle aware components provides an observer pattern to keep the UI and data in sync. There is no need to implement handling in lifecycle methods, and thus provide a way to keep data in sync with lifecycle events in a maintainable, succinct way.

The use of MVVM (Model-View-ViewModel) was used heavily in this project to ensure that the business logic for each of three fragments including, a Feed page, a Recording page, and a Achievements page, had their business logic separated into the ViewModel. Through do so, any data is persisted, especially in the event of a configuration change.

Alongside MVVM, the Room persistence library was integrated into the project to provide a single source of truth/interface between the Acitivites and data sources (The SQLite database, and the service that handles the location updates). Any data fetched from the database was cached to improve efficiency by preventing the need for constant databse access/querying.

In addition to the components mentioned - Activity and Service, a Content Provider was also implemented so that external applications can gain safe access to the user's running data. To protect user data, and give control to the user and their data, the application implements runtime permissions to bring forth transparency into how the application uses location data.

<p align="center">
  <img src="" width="230" />
</p>
  
## Progress made 🥇

- Implemented the MVVM pattern.
- Implemented the Factory pattern for providing ViewModels for each fragment.
- Implemented the Dependency Inversion Principle to prevent tight coupling between the Repository/data sources and the Activities.
- Implemented a pool of worker threads to offload overhead on the UI threads -> used for database access, and the location service.
- Implemented lifecycle aware components to reduce verbose, spread out logic, thus encouraging maintainability. 
- Implemented ROOM persistence library for abstraction of data sources.
- Implemented SQLite local database for the storage of user's activities.
- Implemented caching to prevent continual, slow querying of the database.
- Implemented permissions and Content Provider to provide safe access to user's stored running data.

