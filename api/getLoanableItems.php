<?php
//retrieve all inventory items for item catalogue
//Done BY HIew Jun Hui



header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

	$itemStatus = "normal";
	$loanable = "1"; 
	//$category = mysqli_real_escape_string($conn, stripslashes($_POST['category']));
	
    $stmt = $conn->prepare ("SELECT id,assetNo,assetDescription,category,location FROM inventory WHERE status = ? AND loanable = ?");
	$stmt->bind_param("ss",$itemStatus,$loanable);
	$stmt->execute();
    $result = $stmt->get_result();
	$stmt -> close();
	
	if($result)
	{
		$loanitems = array();

		
		while ($row = $result->fetch_assoc()) {
       		
			//array_push($loanitems,$row['id'],$row['assetNo'],$row['assetDescription'],$row['category']);
			array_push($loanitems, $row);
			//array_push($loanitems,$row['id']);
			//array_push($loanitems,$row['assetNo']);
			//array_push($loanitems,$row['assetDescription']);
			//array_push($loanitems,$row['category']);

		}
	//,$row['id']
	
		$response = [
			'status' => 'Success',
					//'id'=>$asset_id,
					//'assetNo'=>$assetNo,
            		//'assetDescription'=>$asset,
					//'category'=>$assetcategory,
					
					'loanitems'=> $loanitems
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