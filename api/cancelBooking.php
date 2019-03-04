<?php

//insert loan request into the database when the student fill up the form and submit 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [];
//if($_POST) {
	$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
	$workbenchId = mysqli_real_escape_string($conn, stripslashes($_POST['workbenchId']));
    $DateandTimeStart =  mysqli_real_escape_string($conn, stripslashes($_POST['DateandTimeStart']));
    $Status = mysqli_real_escape_string($conn, stripslashes($_POST['Status']));
	
	$status = 'Cancelled';
	$approvalPo = 'Cancelled';
	

    $deletrowsql= "Update`booking` SET status = '$status' , approvalPo = '$approvalPo'  WHERE `userid`= ? AND `workbenchId`= ? AND `timeFrom`= ? AND `status`= ? ";
	
	$stmt = $conn->prepare ($deletrowsql);
	$stmt->bind_param("ssss", $userId, $workbenchId, $DateandTimeStart,$Status);
    
    if($stmt->execute()){
         $response = [
                        'status' => 'Successfully cancelled'
                    ];
    }
    else{
        $response = [
                        'status' => $conn->error
                    ];
    }

    

//}
echo json_encode($response);
?>