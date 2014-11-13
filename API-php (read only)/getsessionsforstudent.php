<?php

// getstudentsessions.php
// Returns all of the upcoming sessions for a student
// Params: student_id
// Status values: "ok", "error"
// Additional JSON elements: JSON array of sessions on success, errno on error

include "util.php";

$query = "SELECT s.id AS session_id," .
	" c.name AS course_name," .
	" t.first_name AS teacher_first_name," .
	" t.last_name AS teacher_last_name," .
	" s.start_time, s.end_time, s.lat, s.lng" .
	" FROM courses c, teachers t, sessions s" .
	" JOIN roster r" .
	" ON r.course_id = s.course_id" .
	" AND r.student_id = " . $_GET["student_id"] .
	" WHERE t.id = c.teacher_id" .
	" AND s.course_id = c.id" .
	" AND s.end_time >= UNIX_TIMESTAMP()" .
	" AND s.start_time <= UNIX_TIMESTAMP() + 604800" .
	" ORDER BY s.start_time ASC";

if($result = mysql_query($query)) {

	$sessions = array();
	while($entity = mysql_fetch_array($result, MYSQL_ASSOC)) {
		array_push($sessions, $entity);
	}

	echo json_encode(array("status" => "ok", "sessionArray" => $sessions));

} else {
	echo json_encode(array("status" => "error", "errno" => mysql_errno() ));
}

?>
