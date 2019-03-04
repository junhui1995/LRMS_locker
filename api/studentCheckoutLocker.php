<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];


$lockerId = mysqli_real_escape_string($conn, stripslashes($_POST['lockerId']));
$loanId = mysqli_real_escape_string($conn, stripslashes($_POST['loanId']));
$status  = 'Available';

$sql = "Update locker SET status = '$status' WHERE id = ?"; 

$stmt = $conn->prepare ($sql);
$stmt->bind_param("i",$lockerId);

if($stmt->execute())
{
		
		//Update loan's status to on loan
		$status  = 'On Loan';
		$sql = "Update loan SET status = '$status' WHERE lid = ?"; 

		$updatestmt = $conn->prepare ($sql);
		$updatestmt->bind_param("i",$loanId);

		if($updatestmt->execute())
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
else
{
	$response =
			[
				'status' => 'Fail'
			];
}


echo json_encode($response);
?>