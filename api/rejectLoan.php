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
	$loanId = mysqli_real_escape_string($conn, stripslashes($_POST['loanId']));
	$reason = mysqli_real_escape_string($conn, stripslashes($_POST['reason']));
	$inventoryId = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryId']));
	$status = 'Rejected';
	$loanable = "1";

	//Update the loan table
	$sql = "UPDATE `loan` SET poId = ?, status = ?, rejectReason = ? WHERE lid = ?"; 
	
	$stmt = $conn->prepare ($sql);
	$stmt->bind_param("ssss", $poId, $status, $reason, $loanId);
	$stmt->execute();

	$result_loanTable = $stmt->affected_rows;
	$stmt -> close();
	
	

	if($result_loanTable > 0)
	{
		
		//Update the inventory table
		$sql2 = "UPDATE inventory SET loanable = '$loanable' WHERE id = ?";
		
		$stmt = $conn->prepare ($sql2);
		$stmt->bind_param("s", $inventoryId);
		$stmt->execute();

		$result_inventoryTable = $stmt->affected_rows;
		
		if($result_inventoryTable > 0)
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