<?php
//PO check in asset
//DOne by Hiew Jun Hui

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [];

if($_POST) {
	$inventoryId = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryId']));
	$loanId = mysqli_real_escape_string($conn, stripslashes($_POST['loanId']));
	//$returnId = mysqli_real_escape_string($conn, stripslashes($_POST['returnId']));
	$poId = mysqli_real_escape_string($conn, stripslashes($_POST['poId']));
	$remarks = mysqli_real_escape_string($conn, stripslashes($_POST['remarks']));
	$status = 'Returned';
	
	
	$stmt = $conn->prepare("UPDATE loan SET status = ? WHERE lid = ?");
	$stmt->bind_param("ss", $status, $loanId);

	$stmt->execute();

	$result_loanTable = $stmt->affected_rows;
	$stmt -> close();
	

	if($result_loanTable > 0)
	{

		//loanitemreturn update
			$stmt = $conn->prepare("UPDATE loanitemreturn SET poId = ?, status = ?, remarks = ? WHERE loanId = ?");
		//$stmt->bind_param("sss", $status, $remarks, $returnId);
		$stmt->bind_param("ssss", $poId, $status, $remarks, $loanId);

		$stmt->execute();

		$result_returnTable = $stmt->affected_rows;
		$stmt -> close();
			if ($result_returnTable > 0)
			{
			$stmt = $conn->prepare("UPDATE inventory SET loanable = 1 WHERE id = ?");
			$stmt->bind_param("i", $inventoryId);
			$stmt->execute();
		
	
			$result_inventoryTable = $stmt->affected_rows;
		
			$stmt -> close();
			if($result_inventoryTable > 0)
			{

				$response = ['status' => 'Success' ];	
			} 
			else 
			{

			$response = ['status' => 'Fail'];
			}
		
			}
			else
			{
				$response = ['status' => 'Fail1'];
			}
	}

	else
	{
		$response = ['status' => 'Fail2'];
	}

}

echo json_encode($response);
?>