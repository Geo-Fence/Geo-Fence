Geo-Fence
=========

This attendance-keeping Android application allows students to check in to class sessions and records their attendance records for later review by professors or administrators. The application uses GPS geofencing to ensure that attendance records are accurate and reflect the actual time that students are present. All data is stored in a remote database, and the application communicates with the database using a custom-build API architecture built with MySQL, PHP, HTTP requests, and JSON encoding.

Average use case:
A student arrives to class and opens the Roll Call app. The student sees a list of upcoming class sessions. He/She selects the top session, then taps the “Check in” button. Roll Call uses GPS location to verify that the user is present, and records his/her arrival time in a remote database. When the user leaves the session, Roll Call updates the attendance record with his/her departure time
