<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];
if($_POST) {
	$poid = mysqli_real_escape_string($conn, stripslashes($_POST['poid']));
	$checkoutStatus = "Pending Receive";

	$sql = "SELECT * FROM loan WHERE poId = ? AND checkOutStatus = ?";
	
	$stmt = $conn->prepare ($sql);
	$stmt->bind_param("ss", $poid, $checkoutStatus);
	$stmt->execute();

	$result = $stmt->get_result();

	if($result)
	{
		$asset = array();
		$asset_id = array();
		$sat = false;

		while ($row = $result->fetch_assoc()) {
			$sat = true;
        	array_push($asset, $row['lid']);
    	}
    	if($sat == true)
    	{
			$response = [
				'status' => 'Success',
	            'categories'=>$asset
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

}
echo json_encode($response);
?>