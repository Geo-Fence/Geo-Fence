<?php

// checkin.php
// Add an entry to the attendance table
// Params: student_id, session_id
// Status values: "ok", "error"
// Additional JSON elements: errno on error

include "util.php";

$query = "INSERT INTO attendance (student_id, session_id, time_in)" .
	" VALUES (" . $_GET["student_id"] . ", " . $_GET["session_id"] . ", UNIX_TIMESTAMP())";

if(mysql_query($query)) {
	echo json_encode(array("status" => "ok"));
} else {
	echo json_encode(array("status" => "error", "errno" => mysql_errno()));
}

?>
