<?php

$mysql = mysql_connect(cus-linux2, tud04734, aweevaim);
$db = mysql_select_db("Fa14_4340_GeoFence", $mysql);

$query = "INSERT INTO attendance (student_id, session_id, time_in)" .
        " VALUES (" . $_GET["student_id"] . ", " . $_GET["session_id"] . ", UNIX_TIMESTAMP())";

if(mysql_query($query)) {
        echo "success";
} else {
        echo "present";
}

?>
