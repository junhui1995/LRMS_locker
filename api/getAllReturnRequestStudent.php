<?php

//retrieve inventory details when po scan QR

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [];
if($_POST) {

	$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));

	

	$sql = "SELECT loanreturn.id,loanreturn.loanId,loanreturn.time,loanreturn.place,loanreturn.status,loanreturn.rejectReason,loanreturn.poId,loanreturn.remarks, loan.inventoryId FROM loanreturn INNER JOIN loan ON loanreturn.loanId = loan.lid AND loan.userId = ?"; 
	$stmt = $conn->prepare ($sql);
	$stmt->bind_param("s", $userId);
	$stmt->execute();

	$result = $stmt->get_result();
	
	$userloanDetails = array();
	$intDetails = array();

	while ($row = $result->fetch_assoc()) 
	{
    	array_push($userloanDetails, $row);
    	$intId = $row['inventoryId'];
		$insql = "SELECT * FROM inventory WHERE id = ?";
		$instmt = $conn->prepare($insql);
		$instmt->bind_param("i", $intId);
		$instmt->execute();
		$inresult = $instmt->get_result();
		$inrow = $inresult->fetch_assoc();
		if($inrow)
		{
			array_push($intDetails, $inrow['assetDescription']);
		}
    }

    if($userloanDetails != null && $intDetails != null)
    {
		$response = [
		 'status' => 'Success',
		'userloanDetails' => $userloanDetails,
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
echo json_encode($response);
?>