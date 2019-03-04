<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

$job = mysqli_real_escape_string($conn, stripslashes($_POST['job']));
$folderId = mysqli_real_escape_string($conn, stripslashes($_POST['folderID']));

if ($job == 'check')
{
	$stmt = $conn->prepare ("SELECT 1 FROM page WHERE folderId = ?");
}
else if ($job == 'retrieve')
{
	$stmt = $conn->prepare("SELECT name, instructions, pageNumber, media FROM page WHERE folderId = ? ORDER BY pageNumber ASC");
}

$stmt->bind_param("i", $folderId);
$stmt->execute();

$result = $stmt->get_result();

if($result)
{
	if ($job == 'check')
	{
		if ($row = $result->fetch_assoc()){
			$response = [
			'status' => 'Success'
			];
		}else {
			$response = [
			'status' => 'No Record Found'
			];
		}
	}
	else if ($job == 'retrieve')
	{
		$folderList = array();
		while ($row = $result->fetch_assoc()) {
			array_push($folderList, $row);
		}
		if(sizeof($folderList) != 0 ){
				$response = [
				'status' => 'Success',
				'folderList'=> $folderList
			];
		}
		else
		{
			$response = [
			'status' => 'No Record Found'
			];
		}
	}
	

}
else
{
	$response =
		[
		'status' => 'Fail'
		];
}

$stmt->close();
echo json_encode($response);
?>