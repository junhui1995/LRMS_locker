<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

$folderId = mysqli_real_escape_string($conn, stripslashes($_POST['folderID']));

if ($folderId == 'root')
{
	$stmt = $conn->prepare ("SELECT * FROM folder WHERE parentId is NULL");
}
else
{
	$stmt = $conn->prepare ("SELECT * FROM folder WHERE parentId = ?");		
	$stmt->bind_param("i", $folderId);
}


$stmt->execute();

$result = $stmt->get_result();
$stmt -> close();

if($result)
{
	$folderList = array();
	
	while ($row = $result->fetch_assoc()) {		
    	array_push($folderList, $row);
	}
	
	if(sizeof($folderList) != 0 ){
			$response = [
			'status' => 'Success',
	        'folderList'=>$folderList,
			
		];
	}
	else
	{
		$response = [
		'status' => 'No Record Found'
        ];
	}

}
else
{
	$response =
		[
		'status' => 'Fail'
		];
}


echo json_encode($response);
?>