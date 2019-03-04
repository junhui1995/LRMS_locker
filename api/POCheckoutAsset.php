<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];

if($_POST) {

	$loanId = mysqli_real_escape_string($conn, stripslashes($_POST['loanId']));
	$status = 'Checked Out';
	
	
	$stmt = $conn->prepare("UPDATE loan SET status = ? WHERE lid = ?");
	$stmt->bind_param("ss", $status, $loanId);

	$stmt->execute();

	$result_loanTable = $stmt->affected_rows;
	$stmt -> close();

	if($result_loanTable > 0)
	{
		$response = ['status' => 'Success'];	
	}
	else
	{
		$response = ['status' => 'Fail'];
	}
}

echo json_encode($response);
?>