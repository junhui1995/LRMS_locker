<?php

//insert loan request into the database when the student fill up the form and submit 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [];
//if($_POST) {
	$loanable = '1';
	$status = 'Cancelled';
	$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
	$lid = mysqli_real_escape_string($conn, stripslashes($_POST['lid']));
	$inventoryId = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryId']));

    $UpdateloanstatusSql= "Update loan SET status = '$status' WHERE lid = ? ";
	
	$Updateloanstatusstmt = $conn->prepare ($UpdateloanstatusSql);
	$Updateloanstatusstmt->bind_param("i",$lid);
    
	$updateINVloanablesql= "Update inventory SET loanable = '$loanable' WHERE id = ? ";
	
	$updateINVloanablestmt = $conn->prepare ($updateINVloanablesql);
	$updateINVloanablestmt->bind_param("i",$inventoryId);
	
    if($Updateloanstatusstmt->execute()){
		if($updateINVloanablestmt->execute())
		{	
			$response = [
                        'status' => 'Successfully cancelled'
                    ];
		}
		else{
        $response = [
                        'status' => $conn->error
                    ];
		}

    }
    else{
        $response = [
                        'status' => $conn->error
                    ];
    }

    

//}
echo json_encode($response);
?>