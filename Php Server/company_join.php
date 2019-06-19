<?php
if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';
    $userId =(int) $_POST['userId'];   
    $companyId=(int)$_POST["companyId"];


    $query=$conn->query("Select * from company_join where userId=$userId and companyId=$companyId");

    $count=$query->rowCount();

    if($count==0){       
        $result["success"]=true; 
        $query=$conn->prepare("INSERT INTO company_join SET
        userId=?,
        companyId=?");

        $insert=$query->execute(array(
        $userId,$companyId
        ));

        if($insert){
            $result["message"]="Katılım isteği başarıyla gönderildi";
        }
    }else{
        $result["success"]=true;
        $result["message"]="Zaten istek gönderilmiş";
    }
    echo json_encode($result);
    $conn=null;
}

?>