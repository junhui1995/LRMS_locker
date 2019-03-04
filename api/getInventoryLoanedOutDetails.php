<?php

//retrieve inventory details when po scan QR, they will retrive all the details from the inventory first



header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

	$response = [];
	$inventoryId = mysqli_real_escape_string($conn, stripslashes($_POST['Iid']));
	// this is just creating variable to store what ever you have pass in $_POST['Iid'] in to invertoryId. 
    //Query to select either email or userid
    
	
	$stmt = $conn->prepare ("SELECT loan.dateFrom, loan.dateto,inventory.id,inventory.serialNo,inventory.inventoryNo,inventory.location,inventory.name,inventory.assetDescription,inventory.status,inventory.category FROM loan INNER JOIN inventory ON loan.inventoryId = ? ");
	$stmt->bind_param("i",$inventoryId);
	$stmt->execute();
    $result = $stmt->get_result();
	
	if($result)
	{
		while($row =$result->fetch_assoc()){
			$response = [

			 'status' => 'Success',
			 'itemDetails' =>[
				'itemName' => $row['name'],
				'inventoryId' => $inventoryId ,
				'serialNo' => $row['serialNo'],
	            'location'=>$row['location'],
	            'assetDescription'=>$row['assetDescription'],
	            'assetConditionStatus'=>$row['status'],
	            'category'=>$row['category'],
			'dateFrom'=>$row['dateFrom'],
			'dateto'=>$row['dateto']
	        ]
		];


		
		}
		
	}
	else
	{
		$response =
			[
			'status' => $conn->error
			];
	}
		
echo json_encode($response);
?>