<?php


header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [];
//if($_POST) {
    $arrayOfWBIDs = array();
    $arrayOfWB = array();
    $arrayOfLocation = array();
	
	$stmt = $conn->prepare ("SELECT * FROM workbench");
	$stmt->execute();
    $result = $stmt->get_result();

	if($result)
	{
		while ($row = $result->fetch_assoc()) {
        	array_push($arrayOfWB, $row['name']);
            array_push($arrayOfWBIDs, $row['id']);
            $labid = $row['labId'];
			$stmt2 = $conn->prepare("SELECT location FROM lab WHERE id = ?;");
			$stmt2->bind_param("i",$labid);
			$stmt2->execute();
			$result2 = $stmt2->get_result();
			
            if($result2)
            {
                while ($row2 = $result2->fetch_assoc()){
                    array_push($arrayOfLocation, $row2['location']);
                }
            }
            
    	}
    	$response = [
			'status' => 'Success',
            'WorkBenchs' => $arrayOfWB,
            'Location' => $arrayOfLocation,
            'WorkBenchID'=> $arrayOfWBIDs
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