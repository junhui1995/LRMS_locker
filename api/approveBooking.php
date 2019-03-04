<?php
//retrieve inventory items when catogory is selected.
//this will be for the item drop down list

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];

if($_POST) {

	$poId = mysqli_real_escape_string($conn, stripslashes($_POST['poId']));
	$bookingId = mysqli_real_escape_string($conn, stripslashes($_POST['bid']));
	$status = 'approved';

	$sql = "UPDATE `booking` SET approvalPo = ?, status = ? WHERE bid = ?";
	
	$stmt = $conn->prepare ($sql);
	$stmt->bind_param("sss", $status, $status, $bookingId);
	$stmt->execute();
	
	$result = $stmt->affected_rows;

	if($result > 0)
	{
		$response =
		[
			'status' => 'Success'
		];
	}

	else
	{
		$response =
		[
			'status' => 'Fail'
		];
	}
}

echo json_encode($response);