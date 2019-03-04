<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');
//Check if account is valid

$response = [
    'status' => 'HELLO'
];

$attendanceQR = mysqli_real_escape_string($conn, stripslashes($_POST['attendanceQR']));

$sql = "SELECT moduleId,labId,topicId FROM attendanceqr WHERE tempKey = ?";

$stmt = $conn->prepare ($sql);
$stmt->bind_param("s", $attendanceQR);
$stmt->execute();
$result = $stmt->get_result();


if($result)
{
	
	if ($row = $result->fetch_assoc())
	{
		$response = [
			'status' => 'Success',
			'moduleId' => $row['moduleId'],
			'labId' => $row['labId'],
			'topicId' => $row['topicId']
			
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