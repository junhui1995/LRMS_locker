<?php

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [
    'status' => 'Nothing inside'
];
//if($_POST) {
	$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
    $IDOfWorkBench =  mysqli_real_escape_string($conn, stripslashes($_POST['IDOfWorkBench']));
	$timeFrom = mysqli_real_escape_string($conn, stripslashes($_POST['timeFrom']));
	$timeTo = mysqli_real_escape_string($conn, stripslashes($_POST['timeTo']));
	$date = mysqli_real_escape_string($conn, stripslashes($_POST['date']));
	$reason = mysqli_real_escape_string($conn, stripslashes($_POST['reason']));
	$title = mysqli_real_escape_string($conn, stripslashes($_POST['title']));
	$faculty = mysqli_real_escape_string($conn, stripslashes($_POST['faculty']));

    //Temp Default stuff
    $type = '1';
    $status = "pending";
    $approvalPo = "pending";
    $faculty = "ICT";
	$approvalAdmin = "approved";
    $approvalPD = "approved";
  

    //Retrieve labID from workbench's name
    $stmt = $conn->prepare ("SELECT * FROM workbench WHERE id = ? ;");
	$stmt->bind_param("i",$IDOfWorkBench);
	$stmt->execute();
    $result = $stmt->get_result();
	
    if($result){
        while($row = $result->fetch_assoc()){
            $labId = $row['labId'];
        }
    }

	//Insert booking details in db
	$stmt = $conn->prepare("INSERT INTO booking(userid,labId,workbenchId,timeFrom,timeTo,type,date,reason,status,approvalPo,startTime,endTime,faculty,title,approvalAdmin,approvalPD) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"); 
	$stmt->bind_param("siississssssssss",$userId,$labId,$IDOfWorkBench,$timeFrom,$timeTo,$type,$date,$reason,$status,$approvalPo,$timeFrom,$timeTo,$faculty,$title,$approvalAdmin,$approvalPD); 
	$result = $stmt->execute();

	if($result)
	{
    	$response = [
			'status' => 'Success'
		];
	}
	else
	{
		$response =
		[
			'status' => $conn->error
		];
	}

//}

echo json_encode($response);
?>