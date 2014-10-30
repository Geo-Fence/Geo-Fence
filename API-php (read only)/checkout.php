<?php

$mysql = mysql_connect(cus-linux2, tud04734, aweevaim);
$db = mysql_select_db("Fa14_4340_GeoFence", $mysql);

$query = "UPDATE attendance" .
        " SET time_out=UNIX_TIMESTAMP()" .
        " WHERE student_id=" . $_GET["student_id"] .
        " AND session_id=" . $_GET["session_id"];

if(mysql_query($query)) {
        echo "success";
} else {
        echo "failure";
}

?>

