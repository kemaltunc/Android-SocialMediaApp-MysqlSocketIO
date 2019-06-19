<?php

if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';
    $userId =(int) $_POST['userId'];   
    $companyId=(int)$_POST["companyId"];
    $type=$_POST["type"];

    if($type=="accept"){
       
        $query=$conn->prepare("Update users set
        companyId=:companyId
        where id=:userId");

        $update=$query->execute(array(
        "companyId"=>$companyId,
        "userId"=>$userId));

        if($update){
            $query=$conn->prepare("Delete from company_join where userId=? and companyId=?");
            $delete=$query->execute(array(
            $userId,$companyId
            ));
          $result["message"]="İstek kabul edildi";
        }
    }
    else{
       $result["message"]="İstek reddedildi";
       $query=$conn->prepare("Delete from company_join where userId=? and companyId=?");
       $delete=$query->execute(array(
       $userId,$companyId
       ));
    }

  
    echo json_encode($result);
    $conn=null;
}

?>