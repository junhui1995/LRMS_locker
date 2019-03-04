<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

if($_POST) {

	$poId = mysqli_real_escape_string($conn, stripslashes($_POST['poId']));
	$returnId = mysqli_real_escape_string($conn, stripslashes($_POST['returnId']));
	$reason = mysqli_real_escape_string($conn, stripslashes($_POST['reason']));
	$status = 'Rejected';

	$sql = "UPDATE loanreturn SET poId = ?, status = ?, rejectReason = ? WHERE id = ?"; 
	
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("sssi", $poId, $status, $reason, $returnId);
	$stmt->execute();

	$result_returnTable = $stmt->affected_rows;
	$stmt -> close();
	
	

	if($result_returnTable > 0)
	{
		$response = [
			'status' => 'Success',
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
?>