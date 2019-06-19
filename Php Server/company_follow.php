<?php


if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    require_once 'connect.php';
    $type = $_POST['type'];

    $userId=$_POST["myId"];
    $companyId=$_POST["id"];

    if($type=="true"){
        $query = $conn->prepare("DELETE FROM follower WHERE userId = :userId and companyId=:companyId" );
        $delete = $query->execute(array(
           'userId' => $userId,
           'companyId'=>$companyId
        ));
    }else{
        $query=$conn->prepare("INSERT INTO follower SET
        userId=?,
        companyId=?");
        
        $insert=$query->execute(array(
          $userId,$companyId
        ));
    
    }

    echo json_encode($result);
    $conn = null;

}