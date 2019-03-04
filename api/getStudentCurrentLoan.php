<?php

//retrieve inventory details when po scan QR

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [];

	$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
	$inventoryId = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryId']));
	$status = "On Loan";
    
	$sql = "SELECT * FROM loan WHERE userId = ? AND status = ? AND inventoryId = ?";
	$stmt = $conn->prepare ($sql);
	$stmt->bind_param("ssi",$userId,$status,$inventoryId);
	$stmt->execute();
    $result = $stmt->get_result();
	
	if($result){
		
		$loanDetails = array();
		$intDetails = array();
		
		if($row = $result->fetch_assoc()) 
		{
			array_push($loanDetails, $row);
			
			$intId = $row['inventoryId'];
			$insql = "SELECT * FROM inventory WHERE id = ?";
			$instmt = $conn->prepare ($insql);
			$instmt->bind_param("i",$intId);
			$instmt->execute();
			$inresult = $instmt->get_result();
			$inrow = $inresult->fetch_assoc();
			if($inrow)
			{
				array_push($intDetails, $inrow['assetDescription']);
			}
		}

		if($loanDetails != null && $intDetails != null)
		{
			$response = [
				'status' => 'Success',
				'loanDetails' => $loanDetails,
				'intDetails' => $intDetails
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