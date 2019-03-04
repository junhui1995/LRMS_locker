<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
require_once('conn.php');
//Check if account is valid
$response = [];
if($_POST) {
	$lrms = "LRMS";
	$id = mysqli_real_escape_string($conn, stripslashes($_POST['id']));
	$pass = mysqli_real_escape_string($conn, stripslashes($_POST['pass']));
	$pass = md5($pass.$lrms);
	//Query to select user detail
	$sql = "SELECT * FROM `user` WHERE `userid` = ? AND `password` = ?";
	
	$stmt = $conn->prepare ($sql);
	$stmt->bind_param("ss", $id, $pass);
	$stmt->execute();
	
	$result = $stmt->get_result();
	$row = $result->fetch_assoc();
	
	if($row)
	{
		$response = [
			'status' => 'Success',
			'user_detail' => [
				'id' => $row['userid'],
				'name' => $row['name'],
				'role' => $row['role'],
				'faculty' => $row['faculty'],
                'email' => $row['email']
			]	
		];
	}
	else
	{
		$response =
			[
			'status' => 'Fail'
			];
	}
		
}
echo json_encode($response);
?>