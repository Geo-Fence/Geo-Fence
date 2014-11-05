<?php

// login.php
// Authenticate login credentials and return user.
// Params: email, password
// Status values: "ok", "incorrect_email", "incorrect_password", "error"
// Return values: account JSON object on success, errno on error

include "util.php";

$query = "SELECT id, first_name, last_name, email, password FROM students WHERE email = '" . $_GET["email"] . "'";

$result = mysql_query($query);

if($account = mysql_fetch_assoc($result)) {
        if(strcmp($account["password"], $_GET["password"]) === 0) {
                echo json_encode(array("status" => "ok", "account" => $account));
        } else {
                echo json_encode(array("status" => "incorrect_password"));
        }
} else if(mysql_errno() === 0) {
        echo json_encode(array("status" => "incorrect_email"));
} else {
        echo json_encode(array("status" => "error", "errno" => mysql_errno() ));
}

?>