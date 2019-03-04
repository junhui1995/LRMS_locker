<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];

if($_POST) {

	$poId = mysqli_real_escape_string($conn, stripslashes($_POST['poId']));
	$returnId = mysqli_real_escape_string($conn, stripslashes($_POST['returnId']));
	$loanId = mysqli_real_escape_string($conn, stripslashes($_POST['loanId']));
	$status = 'Approved';
	
	
	$stmt = $conn->prepare("UPDATE loanreturn SET poId = ?, status = ? WHERE id = ? AND status = 'Pending Approval'");
	$stmt->bind_param("sss", $poId, $status, $returnId);
	$stmt->execute();

	$result_returnTable = $stmt->affected_rows;
	$stmt -> close();

	if($result_returnTable > 0)
	{
		$stmt = $conn->prepare("UPDATE loan SET status = 'Pending Return' WHERE lid = ? AND status = 'On Loan'");
		$stmt->bind_param("s", $loanId);
		$stmt->execute();

		$result_loanTable = $stmt->affected_rows;
		$stmt -> close();
		
		if ($result_loanTable > 0){
			$response = ['status' => 'Success'];
		}
		else {
			$stmt = $conn->prepare("UPDATE loanreturn SET status = 'Pending Approval', poId = null WHERE id = ? AND status = 'Approved'");
			$stmt->bind_param("s", $returnId);
			$stmt->execute();
		
			$response = ['status' => 'Fail'];
		}

		
	}
	else
	{
		$response = ['status' => 'Fail'];
	}
}

echo json_encode($response);
?>