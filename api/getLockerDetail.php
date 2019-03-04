<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];

$lockerId = mysqli_real_escape_string($conn, stripslashes($_POST['lockerId']));
$status = "In Use";

$sql = "SELECT * FROM locker WHERE status = ? AND id = ?"; 

$stmt = $conn->prepare ($sql);
$stmt->bind_param("ss", $status, $lockerId);
$stmt->execute();

$result = $stmt->get_result();
if($result)
{
	$response = 
	[
		'status' => 'Success',
		'detail' => $result -> fetch_assoc()
	];
		
}

else
{
	$response =
	[
		'status' => 'Fail'
	];
}


echo json_encode($response);
?>