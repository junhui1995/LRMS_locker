<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];


$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
$lockerId = mysqli_real_escape_string($conn, stripslashes($_POST['lockerId']));
$InUSE = 'In Use';
$status  = 'Checked Out';

$sql = "SELECT loan.lid as loanId , locker.name as name , locker.location as location , locker.pin as pin FROM locker INNER JOIN loan WHERE loan.lockerId = locker.id AND loan.userId = ? AND loan.status = '$status' AND locker.id = ? AND locker.status = '$InUSE'"; 

$stmt = $conn->prepare ($sql);
$stmt->bind_param("si",$userId ,$lockerId);
$stmt->execute();

$result = $stmt->get_result();
if($result)
{
	
	if($row = $result->fetch_assoc())
	{
		$response = 
		[
			'status' => 'Success',
			'lockerdetail' => $row 
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