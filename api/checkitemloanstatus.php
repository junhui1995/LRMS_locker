<?php
//check status of item that user just scanned
//Done By Hiew Jun Hui


header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];


	$inventoryid = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryid']));
	
    $stmt = $conn->prepare("SELECT loanable,location FROM inventory WHERE id=?");
	$stmt->bind_param("s", $inventoryid);
	$stmt->execute();
    	$result = $stmt->get_result();
	$stmt -> close();
	
	if($result)
	{
		$array = array();
		if ($row = $result->fetch_assoc())
		{
		array_push($array,$row);
		}
		if ($row['loanable'] == "1")
	{
	$response = [
			'location' => $row['location'],
			'status' => 'Success'
            		
		];
		
	    	}
		else
		{
		$response =
			[
'loanable' => $array,
			'status' => 'Fail'
			];

		}

	}
	else
	{
		$response =
			[
			'status' => 'Fail1'
			];
	}
	
echo json_encode($response);
?>