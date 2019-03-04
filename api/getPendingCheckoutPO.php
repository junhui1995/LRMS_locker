<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

$poId = mysqli_real_escape_string($conn, stripslashes($_POST['poId']));
$status = 'Approved';

$sql = "SELECT * FROM loan JOIN inventory ON loan.inventoryId = inventory.id WHERE loan.status = ? AND loan.poId = ? AND loan.dateFrom >= 'CONVERT(date, GETDATE())'" ;

$stmt = $conn->prepare ($sql);
$stmt->bind_param("ss", $status, $poId);
$stmt->execute();

$result = $stmt->get_result();

if($result)
{
	$pendingList = array();
	while ($row = $result->fetch_assoc()) {
    	array_push($pendingList, $row);
	}
	if(sizeof($pendingList) != 0 ){
			$response = [
			'status' => 'Success',
	        'pendingList'=>$pendingList
		];
		}else{
			$response = [
			'status' => 'No Record Found'
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