<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = ['status' => 'no value'];

if($_POST) {

	$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
	
	$sql = "SELECT * FROM loan WHERE userId = ?";
	
	$stmt = $conn->prepare ($sql);
	$stmt->bind_param("s", $userId);
	$stmt->execute();

	$result = $stmt->get_result();

	if($result)
	{
		$inventoryAssetDescription = array();
		$loanList = array();
		$lockerList = array();
		
		while ($row = $result->fetch_assoc()) {
	    	array_push($loanList, $row);
			
			//retrieve inventory description
			$inventoryId = $row['inventoryId'];
			$inventorysql = "SELECT assetDescription FROM inventory WHERE id = ?";
			$inventorysql = $conn->prepare ($inventorysql);
			$inventorysql->bind_param("i", $inventoryId );
			$inventorysql->execute();
			$inventoryResult = $inventorysql->get_result();
			
			if($inventoryResult)
			{
				if($inventoryRow= $inventoryResult->fetch_assoc())
				{
					array_push($inventoryAssetDescription,$inventoryRow['assetDescription']);
				}
			}
			
			
			if($row['lockerRequest'] == 'Yes'){
				
				//use lockerid to retrieve locker details
				$lockerId = $row['lockerId'];
				$lockersql = "SELECT * FROM locker WHERE id = ?";
				$lockerstmt = $conn->prepare ($lockersql);
				$lockerstmt->bind_param("i", $lockerId );
				$lockerstmt->execute();
				
				$lockerResult = $lockerstmt->get_result();
				
				if($lockerResult)
				{
					if($lockerrow = $lockerResult->fetch_assoc())
					{
						array_push($lockerList, $lockerrow);
					}
				}
			}
		}
		
		
		//if reach this point means have retrieve what is needed
		
		if($loanList != null && $inventoryAssetDescription != null){
			
			$response =
			[
				'status' => 'Success',
				'loanDetails' => $loanList,
				'lockerDetails' => $lockerList,
				'assetDescriptionDetails' => $inventoryAssetDescription
			];
		}
	}
}

echo json_encode($response);
?>