<?php

//insert loan request into the database when the student fill up the form and submit 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [];
//if($_POST) {
	$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
	$inventoryId = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryId']));
	$dateFrom = mysqli_real_escape_string($conn, stripslashes($_POST['dateFrom']));
	$dateto = mysqli_real_escape_string($conn, stripslashes($_POST['dateto']));
	$reason = mysqli_real_escape_string($conn, stripslashes($_POST['reason']));
	$faculty = mysqli_real_escape_string($conn, stripslashes($_POST['faculty']));
	$lockerRequest = mysqli_real_escape_string($conn, stripslashes($_POST['lockerRequest']));
	$status = "Pending Approval";
	$loanable = "0";

	//Update inventory loanable status to not loanable anymore once some one made a request
    $stmt = $conn->prepare ("Update inventory SET loanable = '$loanable' WHERE id = ? ");
	$stmt->bind_param("i",$inventoryId);
	$stmt->execute();


	//make sure datatime data is in the correct format before inserted into the database
	$dateFrom = date('Y-m-d H:i:s', strtotime($dateFrom));
	$dateto = date('Y-m-d H:i:s', strtotime($dateto));
	
	$resultAddNewLoan = $conn->query($sql);	

	//Update inventory loanable status to not loanable anymore once some one made a request
    $stmt = $conn->prepare ("INSERT INTO `loan`(`userid`, `inventoryId`, `dateFrom`, `dateto`, `reason`, `status`,`faculty`,`lockerRequest`) VALUES (?,?,?,?,?,?,?,?);");
	$stmt->bind_param("sissssss",$userId,$inventoryId,$dateFrom,$dateto,$reason,$status,$faculty,$lockerRequest);
	

	if($stmt->execute())
	{
    	$response = [
			'status' => 'Success'
		];
	}
	else
	{
		$response =
		[
			'status' => $conn->error
		];
	}

//}
echo json_encode($response);
?>