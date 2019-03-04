<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 


header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

	$assetDescription = "";
	$location = "";
	$itemStatus = "normal";
	$loanable = "1"; 
	$category = mysqli_real_escape_string($conn, stripslashes($_POST['category']));
	$inventoryId = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryId']));

	
    $stmt = $conn->prepare ("SELECT i.assetDescription , i.location, tag.name FROM inventory i LEFT JOIN tag ON tag.tid = i.tagId WHERE i.category = ? AND i.status = ? AND i.loanable = ? AND i.id = ?");
	$stmt->bind_param("ssii",$category,$itemStatus,$loanable,$inventoryId);
	$stmt->execute();
    $result = $stmt->get_result();
	$stmt -> close();
	
	if($result)
	{
		//if result returns true, item is loanable. Therefore, proceed to retrieve details.
		if($row = $result->fetch_assoc()) {
			
			$response = [
					'status' => 'Success',
            		'assetDescription'=>$row['assetDescription'],
            		'id'=>$inventoryId,
					'assetLocation'=>$row['location'],
					'tag'=>$row['name']
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