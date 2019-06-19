<?php


 if ($_SERVER['REQUEST_METHOD'] =='POST'){
    require_once 'connect.php';

    $userId=(int) $_POST['userId'];
    $postId=(int)$_POST['postId'];   
    $type=$_POST['type'];

    if($type=="false"){

        $query=$conn->prepare("INSERT INTO favourite_post SET
        userId=?,
        postId=?");

        $insert=$query->execute(array(
        $userId,$postId
        ));

        if($insert){
            $result["success"]=true;
            $result["action"]="fav";
        }
    }else{
        $query=$conn->prepare("Delete from favourite_post where postId=? and userId=?");
        $delete=$query->execute(array(
        $postId,$userId
        ));

        if($delete){
            $result["success"]=true;
            $result["action"]="unfav";
        }
    }

    echo json_encode($result);
$conn=null;
}
?>


