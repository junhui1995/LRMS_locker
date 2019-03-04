<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];

if($_POST) {

	$poId = mysqli_real_escape_string($conn, stripslashes($_POST['poId']));
	$loanId = mysqli_real_escape_string($conn, stripslashes($_POST['loanId']));
	$lockerId = mysqli_real_escape_string($conn, stripslashes($_POST['lockerId']));
	$pin = mysqli_real_escape_string($conn, stripslashes($_POST['pin']));
	$status = 'Approved';
	$locker_status = 'In Use';
	
	if($lockerId == "No Locker"){
		$status = 'On Loan';
		$stmt = $conn->prepare("UPDATE loan SET poId = ?, status = ? WHERE lid = ?");
		$stmt->bind_param("sss", $poId, $status, $loanId);
	} else {
		$stmt = $conn->prepare("UPDATE loan SET poId = ?, status = ?, lockerId = ? WHERE lid = ? AND status = 'Pending Approval'");
		$stmt->bind_param("ssss", $poId, $status, $lockerId, $loanId);
	}

	$stmt->execute();

	$result_loanTable = $stmt->affected_rows;
	$stmt -> close();

	if($result_loanTable > 0)
	{
		if($lockerId == "No Locker")
		{
			$response = ['status' => 'Success'];
		}
		else
		{
			$stmt = $conn->prepare("UPDATE locker SET status = ?, pin = ? WHERE id = ? AND status = 'Available'");
			$stmt->bind_param("sss", $locker_status, $pin, $lockerId);
			$stmt -> execute();
			
			$result_lockerTable = $stmt->affected_rows;
			$stmt -> close();
			
			if($result_lockerTable > 0)
			{
				$response = ['status' => 'Success'];
			}
			else
			{
				$status = "Pending Approval";
				$stmt = $conn->prepare("UPDATE loan SET poId = null, lockerId = null, status = ? WHERE lid = ? AND status = 'Approved'");
				$stmt->bind_param("ss", $status, $loanId);
				$stmt->execute();
				
				$response = ['status' => 'Fail'];
			}
		}
	}
	else
	{
		$response = ['status' => 'Fail'];
	}
}

echo json_encode($response);
?>