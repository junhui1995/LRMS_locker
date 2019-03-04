<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];


$status = "Available";

$sql = "SELECT * FROM locker WHERE status = ?"; 

$stmt = $conn->prepare ($sql);
$stmt->bind_param("s", $status);
$stmt->execute();

$result = $stmt->get_result();

$lockerList = array();
if($result)
{
	while ($row = $result->fetch_assoc()) {
		array_push($lockerList, $row);
	}
	if(sizeof($lockerList) != 0 ){
		$response = 
		[
			'status' => 'Success',
			'lockerList' => $lockerList
		];
	}
	else
	{
		$response = 
		[
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