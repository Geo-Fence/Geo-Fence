<?php

// enroll.php
// Adds an entry to the roster table
// Params: student_id, enrollment_code
// Status values: "ok", "error"
// Additional JSON elements: errno on error
// Note: errno 0 means that the registration code was invalid

include "util.php";

$student_id = mysql_real_escape_string($_GET["student_id"]);
$enrollment_code = mysql_real_escape_string($_GET["enrollment_code"]);

$query = "SELECT id FROM courses c".
        " WHERE c.enrollment_code='{$enrollment_code}'";
$result = mysql_query($query);

if($result) {
        $course = mysql_fetch_assoc($result, MYSQL_ASSOC);

        if($course) {
                $query = "INSERT INTO roster (student_id, course_id) VALUES (" . $_GET["student_id"] . ", " . $course["id"] . ")";
                if(mysql_query($query)) {
                        echo json_encode(array("status" => "ok"));
                } else {
                        echo json_encode(array("status" => "error", "errno" => mysql_errno()));
                }
        } else {
                echo json_encode(array("status" => "error", "errno" => mysql_errno()));
        }
} else {
        echo json_encode(array("status" => "error", "errno" => mysql_errno()));
}

?>
