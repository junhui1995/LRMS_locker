<?php

//insert loan request into the database when the student fill up the form and submit 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");

require_once('conn.php');

//Check if account is valid

$response = [
    'status' => 'Nothing inside'
];
//if($_POST) {
    $arrayOfBookingDetails = array();
    $arrayOfWorkbenchnameAndLocation = array();
	$userId = mysqli_real_escape_string($conn, stripslashes($_POST['userId']));


    //Retrieve booking details
    $retrieveBookingDetailsSQL = "SELECT * FROM booking WHERE userid = ?";
	
	$stmt = $conn->prepare ($retrieveBookingDetailsSQL);
	$stmt->bind_param("s", $userId);
	$stmt->execute();

	$bookingInformationresult = $stmt->get_result();

	if($bookingInformationresult){
        while($row = $bookingInformationresult->fetch_assoc()){
            array_push($arrayOfBookingDetails, $row);
            $labID = $row['labId'];
			
			//retrieve location and name of workbench
            $retrieveWorkBenchLocationANDnameSQL = "SELECT location ,name FROM lab WHERE id = ?";
			
			$stmt2 = $conn->prepare ($retrieveWorkBenchLocationANDnameSQL);
			$stmt2->bind_param("s", $labID);
			$stmt2->execute();

			$locationANdnameInformationResult = $stmt2->get_result();
			
            if($locationANdnameInformationResult){
                while($row2 = $locationANdnameInformationResult->fetch_assoc()){
                        array_push($arrayOfWorkbenchnameAndLocation,$row2); 
                }
              
            }
  
        }
    }
	
	if($arrayOfBookingDetails != null && $arrayOfWorkbenchnameAndLocation != null)
    {
		$response =
            [	
				'status' => 'Success',
                'BookingDetails' => $arrayOfBookingDetails,
                'WKBLocationAndNAMEDetails' => $arrayOfWorkbenchnameAndLocation
            ];
  
    }
	else
	{
		$response =
			[
				'status' => 'Fail'
			];
	}
	
		 
    

//}
echo json_encode($response);
?>