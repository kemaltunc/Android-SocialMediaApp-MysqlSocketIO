<?php

if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';
 

    $postId=$_POST["postId"];   
    $type=$_POST["type"];


    if($type=="write"){
        $userId=$_POST['userId'];
        $content=$_POST["content"];
        $query=$conn->prepare("INSERT INTO post_comment Set
        content=?,
        userId=?,
        postId=?");
        
        $insert=$query->execute(array($content,$userId,$postId));
        
        if($insert){     
            $result["success"]=true;

        }else{
            $result["success"]=false;
        }

        $content="Gönderine yorum yaptı";
        $query=$conn->prepare("INSERT INTO notifications SET
        content=?,
        userId=?,
        postId=?");

        $insert=$query->execute(array(
        $content,$userId,$postId
        ));


    }
    else{
        $result['comment_list'] = array();
        $query = $conn->query("SELECT * FROM post_comment INNER JOIN users ON post_comment.userId=users.id where post_comment.postId=$postId order by post_comment.id desc");

        if($query){
            $result['success'] = true;
            foreach ($query as $row) {              
             
                $index["name"] = $row["name"];
                $index["surname"] = $row["surname"];
                $index["userImage"] = $row["image_url"];
                $index["content"] = $row["content"];
                array_push($result['comment_list'], $index);
            }
        }
        else{
            $result['success'] = false;
        }
    }
     
   echo json_encode($result);
   $conn=null;
}

?>

