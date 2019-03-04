<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$poId = mysqli_real_escape_string($conn, stripslashes($_POST['poId']));
$response = [];


//$sql = "SELECT loan.userId as userId, inventory.assetNo as assetNo, inventory.assetDescription as assetDescription, lr.time as time, lr.place as location, lr.id as id, lr.loanId as lid, loan.inventoryid as inventoryId FROM loanreturn lr JOIN loan ON lr.loanId = loan.lid JOIN inventory ON loan.inventoryid = inventory.id WHERE lr.poId = ? AND lr.status = ? AND lr.time >= 'CONVERT(date, GETDATE())' AND loan.status = 'Pending Return' ORDER BY lr.time";

$sql = "SELECT lir.userId as userId, inventory.assetNo as assetNo, inventory.assetDescription as assetDescription,lir.loanId as loanid,locker.name as lockername, locker.pin as pin, lir.inventoryId as inventoryId FROM loanitemreturn lir JOIN inventory ON lir.inventoryid = inventory.id JOIN locker ON lir.lockerid = locker.id WHERE lir.status= 'Pending Return'";
//$sql = "SELECT * FROM loanitemreturn WHERE status = ?";
$stmt = $conn->prepare($sql);
//$stmt->bind_param("s", $status);
$stmt->execute();

$result = $stmt->get_result();

if($result)
{
	$returnList = array();
	while ($row = $result->fetch_assoc()) {
    	array_push($returnList, $row);
	}
	if(sizeof($returnList) != 0 ){
			$response = [
			'status' => 'Success',
	        'returnList'=>$returnList
		];
	}
	else
	{
		$response = [
		'status' => 'No Record Found'
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