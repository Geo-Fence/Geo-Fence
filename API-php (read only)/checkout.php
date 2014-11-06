<?php

// checkout.php
// Update an attendance table entry with a new end_time
// Params: student_id, session_id
// Status values: "ok", "error"
// Additional JSON elements: errno on error

include "util.php";

$query = "UPDATE attendance" .
        " SET time_out=UNIX_TIMESTAMP()" .
        " WHERE student_id=" . $_GET["student_id"] .
        " AND session_id=" . $_GET["session_id"];

if(mysql_query($query)) {
        echo json_encode(array("status" => "ok"));
} else {
        echo json_encode(array("status" => "error", "errno" => mysql_errno() ));
}

?>