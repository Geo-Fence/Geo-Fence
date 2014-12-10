Roll Call
=========

This attendance-keeping Android application allows students to check in to class sessions and records their attendance records for later review by professors or administrators. The application uses GPS geofencing to ensure that attendance records are accurate and reflect the actual time that students are present. All data is stored in a remote database, and the application communicates with the database using a custom-build API architecture built with MySQL, PHP, HTTP requests, and JSON encoding.

### Average use case:
A student arrives to class and opens the Roll Call app. The student sees a list of upcoming class sessions. He/She selects the top session, then taps the “Check in” button. Roll Call uses GPS location to verify that the user is present, and records his/her arrival time in a remote database. When the user leaves the session, Roll Call updates the attendance record with his/her departure time

### Features:
* Each user has his/her own account.
* Users can enroll in “courses” which include one or more “sessions.”
* Each session has a time and location.
* A user can only check in to a session when (1) the student is enrolled in the corresponding course, (2) the session is in progress and (3) his/her location is within a close radius to the session’s location.
* If a user leaves a session before it’s over, he/she is checked out.
* When a session ends, all checked in users are checked out.
* Attendance data is stored in a database for later retrieval.

### Implementation:
* PHP for server side database API
* MD5 encryption for user passwords
* Google Play Services
  * Location Services API
   * FusedLocationProviderApi
   * GeofencingApi
* Google Maps Image API for static map thumbnails
* Custom card list UI using fragments
