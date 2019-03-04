<?php
//Done By Hiew Jun Hui
//insert the loan into loan return table  

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');


//Check if account is valid
$response = [];

$status = "Pending Return";
$lid = mysqli_real_escape_string($conn, stripslashes($_POST['lid']));
//First check whether loanId is already in loanreturn table
$stmt = $conn->prepare ("SELECT loanId FROM loanitemreturn WHERE loanId = ? AND status = ?");
$stmt->bind_param("is",$lid,$status);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

//Second check whether loanId is already in loanreturn table
/*$status = "Available";
$stmt = $conn->prepare ("SELECT TOP 1 Lockerid FROM locker WHERE status = ?");
$stmt->bind_param("s",$status);
$stmt->execute();
$result1 = $stmt->get_result();*/
$stmt ->close();
if ($row) {

	//if ($row = $result->fetch_assoc())
	
		$response =
			[
				'status' => 'exist'
			];
	
}

else
	{
	
		//if loanid doesnt exist on loanitemreturn table insert into loanitemreturn  
		$loanId = mysqli_real_escape_string($conn, stripslashes($_POST['lid']));
		$userid = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
		$inventoryid = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryid']));
		$status = "Pending Return";
		
		$stmt1 = $conn->prepare("INSERT INTO loanitemreturn(`loanId`, `userId`, `inventoryId`, `status`) VALUES (?,?,?,?)");
		$stmt1->bind_param("isis",$loanId,$userid,$inventoryid,$status);
		
		//$result2 = $stmt1->execute();
		//$stmt1 -> close();
		if($stmt1->execute())
		{

			
			$stmt2 = $conn->prepare ("UPDATE loan SET status = ? WHERE lid = ? AND userId= ?");
			$stmt2->bind_param("sss",$status,$loanId,$userid);
			$stmt2->execute();			
			$result_loanTable = $stmt2->affected_rows;
			
			if ($result_loanTable >0)
			{
			$response = 
				[
					'status' => 'Success'
				];
			}
			else
			{
			$response =
				[
				'status' => 'Failure'
				];
			}					
		}
		else
		{
			$response =
			[
				'status' => 'Failure1'
			];
		}
    }

	


echo json_encode($response);
?>