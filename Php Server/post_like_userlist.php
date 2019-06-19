<?php

if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';
 
    $postId=(int)$_POST["id"];

    $query=$conn->query("SELECT users.id as userId,name,surname,image_url FROM post_like INNER JOIN users on post_like.userId=users.id where post_like.postId=$postId");

    $result['like_list'] = array();
    if($query){
        $result['success'] = true;
        foreach ($query as $row) { 
            
            $index["userId"]=$row["userId"];
            $index["name"]=$row["name"];
            $index["surname"]=$row["surname"];
            $index["image"]=$row["image_url"];

            array_push($result["like_list"],$index);
            
        }
    }else{
        $result['success'] = false;
    }

    echo json_encode($result);
    $conn=null;
 }
 
 ?>
 
 