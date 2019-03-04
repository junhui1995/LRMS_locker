<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');
//Check if account is valid

$response = [
    'status' => 'Null'
];
	$listOfattendedTime = array();
	$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));
	$moduleId = mysqli_real_escape_string($conn, stripslashes($_POST['moduleId']));
	$labId = mysqli_real_escape_string($conn, stripslashes($_POST['labId']));
	$topicId = mysqli_real_escape_string($conn, stripslashes($_POST['topicId']));
    
	
	//below statement is to check whether student have already attended the session during the day
	$sql = "SELECT attendedTime FROM attendance WHERE studentId = ? AND moduleId = ? AND labId = ?  AND topicId = ?";
	$stmt = $conn->prepare ($sql);
	$stmt->bind_param("siii",$userId,$moduleId,$labId,$topicId);
	$stmt->execute();

	$result = $stmt->get_result();

	if($result)
	{
		
        while($row = $result->fetch_assoc())
		{
			
			//get the date from the attendedTime
			$attendedTime = $row['attendedTime'];
			$attendedDate =	date('Y-m-d',strtotime($attendedTime));
			
			array_push($listOfattendedTime,$attendedDate);
			
		}
		//get the current date
		$currentDate = date('Y-m-d');
			
		//check if student already attended the session for the date
		if(array_key_exists($currentDate,$listOfattendedTime))
		{
			//Insert student attendance
			$sql = "INSERT INTO attendance (studentId,moduleId,labId,topicId) VALUES (?,?,?,?)";
			$insertstmt = $conn->prepare ($sql);
			$insertstmt->bind_param("siii",$userId,$moduleId,$labId,$topicId);
			if($insertstmt->execute())
			{
				//If student's attendance successfully added , find the module he attended and tell user
				$retrieveModuleNamestmt = $conn->prepare ("SELECT name FROM module WHERE mid = ?");
				$retrieveModuleNamestmt->bind_param("i",$moduleId);
				$retrieveModuleNamestmt->execute();
				$retrieveModuleNameresult = $retrieveModuleNamestmt->get_result();
				
				if($retrieveModuleNameresult)
				{
					if($retrieveModuleNamerow = $retrieveModuleNameresult->fetch_assoc())
					{
						$response =
						[	
							'status' => $attendedDate,
							'ModuleName' => $currentDate
							
						];
			
						}
				}
				else
				{
					$response =
					[	
						'status' => 'sorry unable to retrieve module name'
					];
				
				}
					
				
			}
			else
			{
					$response =
					[
						'status' => 'Sorry unable to add in your attendance'
					];
				
			}	
		}
		else
		{
			$response =
		     [
                'status' => 'Same Day'
			 ];
		}
		
		//if list is empty means there is no attended time with the sql executed so insert student attendance
		if(empty($listOfattendedTime))
		{
			//Insert student attendance
			$sql = "INSERT INTO attendance (studentId,moduleId,labId,topicId) VALUES (?,?,?,?)";
			$insertstmt = $conn->prepare ($sql);
			$insertstmt->bind_param("siii",$userId,$moduleId,$labId,$topicId);
			if($insertstmt->execute())
			{
				//If student's attendance successfully added , find the module he attended and tell user
				$retrieveModuleNamestmt = $conn->prepare ("SELECT name FROM module WHERE mid = ?");
				$retrieveModuleNamestmt->bind_param("i",$moduleId);
				$retrieveModuleNamestmt->execute();
				$retrieveModuleNameresult = $retrieveModuleNamestmt->get_result();
				
				if($retrieveModuleNameresult)
				{
					if($retrieveModuleNamerow = $retrieveModuleNameresult->fetch_assoc())
					{
						$response =
						[	
							'status' => 'Success',
							'ModuleName' => $retrieveModuleNamerow['name']
							
						];
				
					}
				}
				else
				{
					$response =
					[	
						'status' => 'sorry unable to retrieve module name'
					];
				
				}
					
				
			}
			else
			{
				$response =
			     [
                     'status' => 'Sorry unable to add in your attendance'
			     ];
				
			}	
		}
        
    }

echo json_encode($response);
?>