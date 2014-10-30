<?php

$mysql = mysql_connect(cus-linux2, tud04734, aweevaim);
$db = mysql_select_db("Fa14_4340_GeoFence", $mysql);

$query = "SELECT s.id AS session_id, c.name AS course_name, t.first_name AS teacher_first_name, t.last_name AS teacher_last_name, s.start_time, s.end_time, s.lat, s.lng" .
        " FROM courses c, teachers t, sessions s" .
        " JOIN roster r" .
        " ON r.course_id = s.course_id" .
        " AND r.student_id = " . $_GET["student_id"] .
        " WHERE t.id = c.teacher_id" .
        " AND s.course_id = c.id" .
        " AND s.end_time >= UNIX_TIMESTAMP()" .
        " ORDER BY s.start_time ASC";

$result = mysql_query($query);
$json = array();

while($entity = mysql_fetch_array($result, MYSQL_ASSOC)) {
        array_push($json, $entity);
}

echo "{\"sessions\":" . json_encode($json) . "}"

?>
