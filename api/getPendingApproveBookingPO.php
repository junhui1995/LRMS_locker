<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

$status = 'pending';

$sql = "SELECT b.bid as bid, b.userid as userid, l.name as labId, w.name as workbenchId, CAST(b.timeFrom AS TIME(0)) as timeFrom, CAST(b.timeTo AS TIME(0)) as timeTo, b.date as date, b.reason as reason FROM booking b JOIN workbench w ON b.workbenchId = w.id JOIN lab l ON b.labId = l.id WHERE b.status = ?";

$stmt = $conn->prepare ($sql);
$stmt->bind_param("s", $status);
$stmt->execute();

$result = $stmt->get_result();

if($result)
{
	$bookingList = array();
	while ($row = $result->fetch_assoc()) {
    	array_push($bookingList, $row);
	}
	if(sizeof($bookingList) != 0 ){
		$response = [
			'status' => 'Success',
	        'bookingList'=>$bookingList
		];
	}
	else
	{
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