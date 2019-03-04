<?php

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');


//Check if account is valid
$response = [];

//First check whether loanId is already in loanreturn table
$stmt = $conn->prepare ("SELECT loanId FROM loanreturn WHERE loanId = ?");
$stmt->bind_param("i",$loanId);
$stmt->execute();
$result = $stmt->get_result();

if($result) {
	
	if($row = $result->fetch_assoc())
	{
		$response =
			[
				'status' => 'Already exist'
			];
	}
	else{
	
		//if loanid doesnt exist on loanreturn table insert return request 
		$loanId = mysqli_real_escape_string($conn, stripslashes($_POST['loanId']));
		$userid = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
		$assetNo = mysqli_real_escape_string($conn, stripslashes($_POST['assetNo']));
		$poId = mysqli_real_escape_string($conn, stripslashes($_POST['poId']));
		$status = "Pending Return";
		$lockerid mysqli_real_escape_string($conn, stripslashes($_POST['lockerid']));
		
		$stmt = $conn->prepare ("INSERT INTO loanreturn(`loanId`, `userId`, `assetNo`,`poId`, `status`, `lockerid`) VALUES (?,?,?,?,?,?)");
		$stmt->bind_param("issssi",$loanId,$userId,$assetNo,$poId,$status,$lockerid);
		
		if($stmt->execute())
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
	
}

echo json_encode($response);
?>