<?php

//insert loan request into the database when the student fill up the form and submit 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [];
//if($_POST) {
	$id = mysqli_real_escape_string($conn, stripslashes($_POST['id']));

    $retrieveWorkBenchLocationsql = "SELECT `labId`,`name` FROM workbench WHERE `id` = ?";
	
	$stmt = $conn->prepare ($retrieveWorkBenchLocationsql);
	$stmt->bind_param("s",$id);
	$stmt->execute();
    $result = $stmt->get_result();

    if($result){
        if($row = $result->fetch_assoc()){
            $workbench = $row['name'];
            $labId = $row['labId'];
            $retrieveWorkBenchLocationsql = "SELECT `location` FROM lab WHERE `id` = ?";
			
			$stmt2 = $conn->prepare ($retrieveWorkBenchLocationsql);
			$stmt2->bind_param("s",$labId);
			$stmt2->execute();
			$result2 = $stmt2->get_result();
			
            if($result2){
                if($row2 = $result2->fetch_assoc()){
                    $location = $row2['location'];
                     $response = [
                        'status' => 'Success',
                        'workbench' => $workbench,
                        'location' => $location
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
           
        }
        else{
				$response = [
                        'status' => 'Sorry no workbench found'
                ];
		}
		
    }


    

//}
echo json_encode($response);
?>