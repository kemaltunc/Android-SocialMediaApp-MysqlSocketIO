<?php


 if ($_SERVER['REQUEST_METHOD'] =='POST'){
    require_once 'connect.php';

    $userId=(int) $_POST['userId'];
    $postId=(int)$_POST['postId'];   
    $type=$_POST['type'];

    if($type=="false"){


        $query=$conn->prepare("INSERT INTO post_like SET
        userId=?,
        postId=?");

        $insert=$query->execute(array(
        $userId,$postId
        ));

        if($insert){
            $result["success"]=true;
            $result["action"]="like";
        }

        $content="Gönderini beğendi";
        $query=$conn->prepare("INSERT INTO notifications SET
        content=?,
        userId=?,
        postId=?");

        $insert=$query->execute(array(
        $content,$userId,$postId
        ));


  
  
    }
    else{
        $query=$conn->prepare("Delete from post_like where postId=? and userId=?");
        $delete=$query->execute(array(
        $postId,$userId
        ));

        if($delete){
            $result["success"]=true;
            $result["action"]="dislike";
        }
    }
    $like=$conn->query("select * from post_like where postId=$postId");
    $likeCount=$like->rowCount();
    $result["likeCount"]=$likeCount;

echo json_encode($result);
$conn=null;
}
?>


