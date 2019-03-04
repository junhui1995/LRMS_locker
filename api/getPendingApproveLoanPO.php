<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

$status = 'Pending Approval';

$sql = "SELECT * FROM loan JOIN inventory ON loan.inventoryid = inventory.id WHERE loan.status = ? AND loan.dateFrom >= 'CONVERT(date, GETDATE())' ORDER BY loan.dateFrom";

$stmt = $conn->prepare ($sql);
$stmt->bind_param("s", $status);
$stmt->execute();

$result = $stmt->get_result();

if($result)
{
	$loanList = array();
	while ($row = $result->fetch_assoc()) {
    	array_push($loanList, $row);
	}
	if(sizeof($loanList) != 0 ){
		$response = [
			'status' => 'Success',
	        'loanList'=>$loanList
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