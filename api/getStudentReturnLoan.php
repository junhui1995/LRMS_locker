<?php

//retrieve inventory details when po scan QR

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [];

	$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
	//$inventoryId = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryId']));
	
	$status = "Pending Return";
    
	//$sql = "SELECT * FROM loanitemreturn WHERE userId = ? AND status = ?";
	$sql = "SELECT loanId,userId,inventoryId,poId, status FROM loanitemreturn WHERE userId = ? AND status = ?";
	$stmt = $conn->prepare ($sql);
	$stmt->bind_param("ss",$userId,$status);
	
    	$stmt->execute();
	$result =$stmt->get_result();
	
	if($result){
		
		$loanDetails = array();
		
		if ($row = $result->fetch_assoc()) 
		{
			array_push($loanDetails, $row);


			$response = [
				'status' => 'Success',
				'loanReturnDetails' => $loanDetails
			];
			

	}	
	else
	{
		$response =
			[				
			'status' => 'fight'
			];
	}
		}		
echo json_encode($response);
?>