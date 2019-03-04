<?php

//retrieve inventory details when po scan QR



header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [];
//if($_POST) {
	//$stmt = $conn->prepare ("SELECT DISTINCT category FROM inventory");
	//above is the original code
	$stmt = $conn->prepare ("SELECT category FROM inventory WHERE loanable = '1' group by category");
	$stmt->execute();
    $result = $stmt->get_result();

	if($result)
	{
		$cat = array();
		while ($row = $result->fetch_assoc()) {
        	array_push($cat, $row['category']);
    	}
    	$response = [
			'status' => 'Success',
            'categories'=>$cat
		];

	}
	else
	{
		$response =
			[
			'status' => 'Fail'
			];
	}

//}
echo json_encode($response);
?>