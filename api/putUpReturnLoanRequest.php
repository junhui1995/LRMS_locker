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
		$time = mysqli_real_escape_string($conn, stripslashes($_POST['time']));
		$place = mysqli_real_escape_string($conn, stripslashes($_POST['place']));
		$status = "Pending Approval";
		
		$stmt = $conn->prepare ("INSERT INTO loanreturn(`loanId`, `time`, `place`, `status`) VALUES (?,?,?,?)");
		$stmt->bind_param("isss",$loanId,$time,$place,$status);
		
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