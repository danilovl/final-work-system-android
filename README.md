# Final work system - android application

## Overview

This Android application is a mobile companion to
the [Final work system](https://github.com/danilovl/final-work-system) - a comprehensive web-based
platform for managing academic final works, thesis projects, and dissertations.

This mobile application extends the functionality of the main system by providing students and supervisors with
convenient mobile access to key features and real-time updates throughout the entire academic
project lifecycle.

## Demonstrations

### Student perspective
Experience the system from a student's viewpoint, including project management, event registration, and academic calendar features.

**Demo Video**:

<div align="center">
  <video src="https://github.com/user-attachments/assets/e8709c2a-4522-44e7-9bdc-523d75acd2f7"
         controls
         style="width: 250px; height: auto; border: 1px solid #ccc; border-radius: 8px;">
  </video>
</div>

### Supervisor perspective
Explore the supervisor interface with project oversight, event creation, and student management capabilities.

**Demo Video**:

<div align="center">
  <video src="https://github.com/user-attachments/assets/9315dcf0-9571-4a1b-b99b-e288acb0460e"
         controls
         style="width: 250px; height: auto; border: 1px solid #ccc; border-radius: 8px;">
  </video>
</div>

## Features

### For students
- **Work management**: View and manage assigned final works and thesis projects
- **Calendar integration**: Access event calendar with important deadlines and meetings
- **Event registration**: Register for thesis defenses, consultations, and academic events
- **Progress tracking**: Monitor project milestones and submission deadlines
- **Communication**: Direct interaction with supervisors and academic staff

### For supervisors
- **Student oversight**: Manage multiple student projects and track their progress
- **Event creation**: Schedule consultations, reviews, and defense sessions
- **Calendar management**: Organize academic events and set important deadlines
- **Work evaluation**: Review and provide feedback on student submissions
- **Administrative tools**: Access comprehensive project management features

### Core functionality
- **Interactive calendar**: Monthly view with event management and scheduling
- **Work list management**: Categorized view of projects by author, supervisor, or status
- **Search capabilities**: Advanced search functionality across all works and events
- **Event details**: Comprehensive event information with location mapping
- **Real-time updates**: Live synchronization of project status and notifications
- **User authentication**: Secure login system with role-based access control

## Getting started

### Prerequisites

#### Development Environment
- **Android Studio**: Hedgehog (2023.1.1) or later recommended
- **Java Development Kit (JDK)**: Java 11 or higher
- **Kotlin**: 1.8+ (automatically managed by Android Studio)
- **Gradle**: 8.0+ (wrapper included in project)

#### Android SDK Requirements
- **Minimum SDK**: Android 14 (API level 34)

### Installation

#### Backend Setup
This Android application requires a backend server to function properly. You need to have the backend project running:

**Backend Project**: [Final work system backend](https://github.com/danilovl/final-work-system)

Please follow the backend project's installation instructions to set up the server before proceeding with the Android application setup.

#### Android application Setup
1. Clone the repository
2. Open the project in Android Studio
3. Configure the required variables in configuration files:

   **in `local.properties`:**
   ```properties
   # API Configuration
   BASE_URL=http://your-backend-server-url/
   API_KEY=your-api-key-here
   API_GOOGLE_KEY=your-google-api-key-here
   ```

   **in `gradle.properties`:**
   ```properties
   # API Configuration
   BASE_URL=http://your-backend-server-url/
   API_KEY=your-api-key-here
   API_GOOGLE_KEY=your-google-api-key-here
   ```

   > **Note**: The `API_KEY` is a unique authentication key that must be added to the `api_user` table in the backend database. This key is used to authenticate API requests between the Android application and the backend server.
   >
   > **Note**: The `API_GOOGLE_KEY` is a Google Maps API key used for map functionality.

4. Sync the project with Gradle files
5. Build and run the application

## License

MIT License

FinalWork application is completely free and released under the [MIT License](https://github.com/danilovl/final-work-system-android/LICENSE).

## Author

Created by [Vladimir Danilov](https://github.com/danilovl).
