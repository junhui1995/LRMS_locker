<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');

$response = [ 'status' => 'null' ];

		$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
		$inventoryId = mysqli_real_escape_string($conn, stripslashes($_POST['inventoryId']));

		$stmt = $conn->prepare("SELECT topicId FROM inventory WHERE id = ?;");
        $stmt->bind_param("i",$inventoryId);
        $stmt->execute();
        $stmt->bind_result($topicId);
        $stmt->fetch();
		$stmt->close();
		$validLoan = 0;
		// If inventory attached to a topic, get minAttendance for the topic
        if ($topicId) {
			$stmt = $conn->prepare("SELECT minAttendance FROM topic WHERE tid = ?;");
	        $stmt->bind_param("i", $topicId);
	        $stmt->execute();
	        $stmt->bind_result($minAttendance);
	        $stmt->fetch();
			$stmt->close();
			// If minAttendance for that topic above 0, check if studen fulfills requirement
			if ($minAttendance > 0) {
				$stmt = $conn->prepare("SELECT COUNT(aid) FROM attendance WHERE studentId = ? AND topicId = ?;");
		        $stmt->bind_param("si", $userId , $topicId);
		        $stmt->execute();
		        $stmt->bind_result($userAttendance);
		        $stmt->fetch();
				$stmt->close();
				if ($userAttendance >= $minAttendance) {
					$validLoan = 1;
					
					$response = [ 'status' => 'Success' ];

				} else {
					$response = [ 'status' => 'Fail' ];

				}
			} else {
				$validLoan = 1;
				
				$response = [ 'status' => 'Success' ];

			}
		} else {
			$validLoan = 1;
			
			$response = [ 'status' => 'Success' ];
		}





echo json_encode($response);
?>