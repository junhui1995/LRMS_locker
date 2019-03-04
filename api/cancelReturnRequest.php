<?php

//insert loan request into the database when the student fill up the form and submit 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [];
//if($_POST) {
	$loanId = mysqli_real_escape_string($conn, stripslashes($_POST['loanId']));
	$status = 'Cancelled';

    $updaterowsql = "Update loanreturn SET status = '$status' WHERE loanId = ? ";
	
	$stmt = $conn->prepare ($updaterowsql);
	$stmt->bind_param("i",$loanId);
    
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