<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];

		$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
		$loanid = mysqli_real_escape_string($conn, stripslashes($_POST['lid']));

//checking if locker has been assigned

			/*$checklocker = $conn->prepare("SELECT lockerid FROM loanitemreturn WHERE userId=? AND loanId=? ");
			$checklocker->bind_param("si", $userId,$loanid);
			$checklocker->execute();
			$result = $checklocker->get_result();
			$lockeridarray = array();
			if ($result)
			{
				if ($row = $result->fetch_assoc()){
					array_push($lockeridarray, $row);
			
					$lockerid = $row['lockerid'];
	
				$response = 
		[
			'status' => 'before',
			'lockerid' =>$lockerid
			
			//'lockerdetail' => $lockerList
		];	
				}*/
			
		
		$status = "Available";
//$sql = "SELECT TOP 1 id FROM locker WHERE status = ?"; 
$sql = "SELECT * FROM locker WHERE status = ? LIMIT 1";
$stmt = $conn->prepare ($sql);
$stmt->bind_param("s", $status);
$stmt->execute();

$result = $stmt->get_result();
if ($result)
{
	$lockerList = array();
	if ($row = $result->fetch_assoc()){
			array_push($lockerList, $row);
			
			$lockerid = $row['id'];
	}
			

		
		$updatelocker = $conn->prepare ("UPDATE loanitemreturn SET lockerid=? WHERE userId=? AND loanId=?");
			$updatelocker->bind_param("isi", $lockerid, $userId,$loanid);
			if($updatelocker->execute())
			{
				$status1="In Use";
				$updatelocker1 = $conn->prepare ("UPDATE locker SET status=? WHERE id=?");
				$updatelocker1->bind_param("si", $status1,$lockerid);
				if ($updatelocker1->execute())
				{
	$response = 
		[
			'status' => 'Success',
			'lockerid' => $lockerid
			//'lockerdetail' => $lockerList
		];
				}
}
}
else
{
	$response = 
		[
			'status' => 'No Locker'
			
			//'lockerdetail' => $lockerList
		];
	
}
				
			
			



echo json_encode($response);
?>