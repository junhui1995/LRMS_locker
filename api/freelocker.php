<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];
//$lockerId = mysqli_real_escape_string($conn, stripslashes($_POST['lockerId']));
$loanId = mysqli_real_escape_string($conn, stripslashes($_POST['loanId']));
		
//setting locker back to available
			$status="Available";
			$number = "1";
			$checklocker = $conn->prepare("UPDATE locker SET status=? WHERE locker.id=?");
			$checklocker->bind_param("ss", $status,$number);
			$checklocker->execute();
			$result = $checklocker->get_result();
			if ($result)
			{
				$response = ['status' => 'Success' ]
				
			}
			
echo json_encode($response);
?>