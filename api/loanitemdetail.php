<?php
//Done By Hiew Jun Hui
//This is for scanning the item and getting the required info


header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

	$assetDescription = "";
	//$location = "";
	$itemStatus = "normal";
	$loanable = "1"; 
	//$category = mysqli_real_escape_string($conn, stripslashes($_POST['category']));
	$inventoryId = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryId']));

	
    //$stmt = $conn->prepare ("SELECT i.assetDescription , i.location, tag.name FROM inventory i LEFT JOIN tag ON tag.tid = i.tagId WHERE i.category = ? AND i.status = ? AND i.loanable = ? AND i.id = ?");
	$stmt = $conn->prepare ("SELECT assetNo,category, assetDescription FROM inventory WHERE id=?");
	//, loanable,status
	//$stmt->bind_param("ssii",$category,$itemStatus,$loanable,$inventoryId);
	$stmt->bind_param("i",$inventoryId);
	$stmt->execute();
    $result = $stmt->get_result();
	$stmt -> close();
	
	if($result)
	{
		//$item = array();
		//if result returns true, item is loanable. Therefore, proceed to retrieve details.
		if($row = $result->fetch_assoc()) {
			//array_push($item,$row);
			
			$response = [
			
					'status' => 'Success',
					'assetNo'=> $row['assetNo'],
            		'assetDescription'=>$row['assetDescription'],
					'category'=>$row['category'],
            		'id'=>$inventoryId
					
					//'assetLocation'=>$row['location'],
					//'tag'=>$row['name']
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