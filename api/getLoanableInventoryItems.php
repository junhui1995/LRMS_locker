<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 


header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

	$itemStatus = "normal";
	$loanable = "1"; 
	$category = mysqli_real_escape_string($conn, stripslashes($_POST['category']));
	
    $stmt = $conn->prepare ("SELECT assetDescription, id, location FROM inventory WHERE category = ? AND status = ? AND loanable = ?");
	$stmt->bind_param("ssi",$category,$itemStatus,$loanable);
	$stmt->execute();
    $result = $stmt->get_result();
	$stmt -> close();
	
	if($result)
	{
		$asset = array();
		$assetlocation = array();
		$asset_id = array();
		
		while ($row = $result->fetch_assoc()) {
       		array_push($asset, $row['assetDescription']);
        	array_push($asset_id, $row['id']);
			array_push($assetlocation, $row['location']);
			
		}
	
	
		$response = [
			'status' => 'Success',
            		'assetDescription'=>$asset,
            		'id'=>$asset_id,
					'assetLocation'=>$assetlocation
		];
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