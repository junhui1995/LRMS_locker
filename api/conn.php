<?php
$servername = "127.0.0.1";	//localhost
//$username = "ITP"; 		//root access (Enable this when on Server)
//$password = "itpadmin";	//root password (Enable this when on Server)
$username = "root"; 		//root access (Enable this when on Localhost)
$password = "";				//root password (Enable this when on Localhost)
$dbname = "labresource";	//database

// Create connection
$conn = new mysqli($servername, $username, $password);

// Select Database
$db_selected = mysqli_select_db($conn,$dbname);

//Check if Database exist
if (!$db_selected) {
	$sql = "CREATE DATABASE IF NOT EXISTS $dbname DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";
	// Query to create database
	if ($conn->query($sql)) {
		$conn = new mysqli($servername, $username, $password, $dbname);
		include '../setup/index.php';
		} else {
			echo "Error creating database: " . $conn->error;
			}
	}
else{
	//Check if there is any table
	$sql = "SHOW TABLES FROM $dbname";
	if (mysqli_num_rows($conn->query($sql))==0) {
		$conn = new mysqli($servername, $username, $password, $dbname);
		include '../setup/index.php';
	}
	else
		$conn = new mysqli($servername, $username, $password, $dbname);
}

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

?>