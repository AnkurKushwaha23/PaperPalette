# PaperPalette 🎨

PaperPalette is a modern Android application built with Jetpack Compose ✍️ that allows you to discover 🔎, save, and manage your favorite digital artwork. Leveraging a robust architecture and the latest Android Jetpack libraries 🚀, PaperPalette offers a smooth and intuitive user experience ✨.

## Built With 🛠️

This project utilizes the following cutting-edge Android technologies and libraries:

* **Jetpack Compose:** A modern declarative UI toolkit for building native Android UIs 📱.
* **Hilt DI:** A dependency injection library for Android that reduces boilerplate and simplifies dependency management 💉.
* **Compose Navigation:** A library for navigating between composable screens within your Android app 🗺️.
* **Room Persistence Library:** Provides an abstraction layer over SQLite for efficient and type-safe local data storage 💾.
* **Paging 3:** A library that helps you load and display pages of data from a larger dataset efficiently, both from local storage and over the network 📄➡️.
* **Retrofit:** A type-safe HTTP client for Android and Java, making it easy to connect to RESTful APIs 🌐.
* **MVVM (Model-View-ViewModel):** An architectural pattern that separates the UI (View) from the application logic (ViewModel), making the code more testable and maintainable 🏗️.

## Features 🎉

* **Browse Artwork:** Discover a curated collection of digital art 🖼️.
* **Download Images:** Save your favorite artwork directly to your device ⬇️.
* **Save Favorites:** Easily save your favorite pieces of art for later viewing ❤️.
* **Offline Access:** Access your saved artwork even when you're offline (thanks to Room Persistence) 📶➡️🚫.
* **Efficient Data Loading:** Experience smooth scrolling through large collections of artwork with Paging 3 💨.
* **Clean and Modern UI:** Enjoy a visually appealing and user-friendly interface built with Jetpack Compose ✨.

## Architecture 🏛️

PaperPalette follows the Model-View-ViewModel (MVVM) architecture, promoting a clear separation of concerns:

* **View (Compose UI):** Responsible for rendering the UI and observing data from the ViewModel 👀.
* **ViewModel:** Holds the UI-related data and exposes it to the View. It also handles user interactions and communicates with the Repository 🧠.
* **Model (Repository, Data Sources, Room Database):** Responsible for handling data logic, including fetching data from network APIs (using Retrofit), caching data locally (using Room), and providing a clean API to the ViewModel 📦. Hilt manages the dependencies within this layer ⚙️.