<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once 'connect.php';

    $userId = $_POST['userId'];

    $query=$conn->query("SELECT notifications.*, name,surname,users.image_url as userImage FROM posts RIGHT JOIN notifications ON posts.id=notifications.postId INNER JOIN users ON notifications.userId=users.id where posts.user_id=$userId");


    $result['notif_list'] = array();

    if($query){
        $result["success"]=true;
        foreach($query as $row){
            $index["id"]=$row["id"];
            $index["content"]=$row["content"];
            $index["userId"]=$row["postId"];
            $index["date"]=$row["created_at"];
            $index["name"]=$row["name"];
            $index["surname"]=$row["surname"];
            $index["image"]=$row["userImage"];
            
            array_push($result["notif_list"],$index);

        }
    }else{
        $result["success"]=false;
    }



    echo json_encode($result);
    $conn = null;


}

?>