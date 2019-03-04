<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');
//Check if account is valid

$response = [
    'status' => 'Null'
];

if($_POST) {
	$date = mysqli_real_escape_string($conn, stripslashes($_POST['date']));

    $workbenchId =  mysqli_real_escape_string($conn, stripslashes($_POST['workbenchId']));

	$stmt = $conn->prepare ("SELECT timeFrom, timeTo FROM booking WHERE date = ? AND workbenchId = ? AND (status = 'approved' || status = 'pending')");
	$stmt->bind_param("si",$date,$workbenchId);
	$stmt->execute();
    $result = $stmt->get_result();
	
	if($result)
	{
		$arrayOfTimeSlotsBooked = array();
        
		while ($row = $result->fetch_assoc()) {
        	array_push($arrayOfTimeSlotsBooked, $row);
    	}
        $response = [
				'status' => 'Success',
	            'TimeSlotsBooked'=> $arrayOfTimeSlotsBooked
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