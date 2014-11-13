<?php

include "util.php";

$first_name = mysql_real_escape_string(urldecode($_GET["first_name"]));
$last_name = mysql_real_escape_string(urldecode($_GET["last_name"]));
$email = mysql_real_escape_string(urldecode($_GET["email"]));
$password = md5(urldecode($_GET["password"]));

$query = "INSERT INTO students (first_name, last_name, email, password)".
		" VALUES ('{$first_name}', '{$last_name}', '{$email}', '{$password}')";

if(mysql_query($query)) {
    echo json_encode(array("status" => "ok"));
} else {
	echo json_encode(array("status" => "error", "errno" => mysql_errno()));
}

?>
