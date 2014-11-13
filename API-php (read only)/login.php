<?php

// login.php
// Authenticate login credentials and return user.
// Params: email, password
// Status values: "ok", "error"
// Additional JSON elements: account object on success, errno on error
// Note: errno 0 means login was unsuccessful

 include "util.php";

 $email = mysql_real_escape_string(urldecode($_GET["email"]));
 $password = md5(urldecode($_GET["password"]));

$query = "SELECT id, first_name, last_name, email, password".
	" FROM students".
	"  WHERE email = '{$email}' AND password = '{$password}'";

$result = mysql_query($query);

if($account = mysql_fetch_assoc($result)) {
    echo json_encode(array("status" => "ok", "account" => $account));
} else {
	echo json_encode(array("status" => "error", "errno" => mysql_errno()));
}

?>
