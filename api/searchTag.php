<?php
//retrieve inventory items when catogory is selected. 
//this will be for the item drop down list 

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid

$response = [];

$tag = mysqli_real_escape_string($conn, stripslashes($_POST['tag']));
$sql = "SELECT f.fid as fid, f.name as name FROM folder f JOIN foldertag ft ON f.fid = ft.folderId JOIN tag t ON ft.tagId = t.tid WHERE t.name LIKE CONCAT('%',?,'%')";

$stmt = $conn->prepare ($sql);
$stmt->bind_param("s",$tag);
$stmt->execute();

$result = $stmt->get_result();


if($result)
{
	$folderList = array();
	while ($row = $result->fetch_assoc()) {
    	array_push($folderList, $row);
	}
	if(sizeof($folderList) != 0 ){
			$response = [
			'status' => 'Success',
	        'folderList'=>$folderList
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