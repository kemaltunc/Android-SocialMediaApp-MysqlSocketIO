<?php


if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';

    $userId=$_POST["userId"];
    $companyId=$_POST["companyId"];
    $content=$_POST["content"];
    $photo=$_POST["photo"];


    $photo_name=md5(uniqid()).".jpeg";
    $path="uploads/images/post_images/$photo_name";

    $query=$conn->prepare("INSERT INTO posts Set
    user_id=?,
    company_id=?,
    content=?,
    image_url=?");

    $insert=$query->execute(array( $userId,$companyId,$content,$photo_name));

    if($insert){
        $result["success"]=true;
        $decoded=base64_decode($photo);
        file_put_contents($path,$decoded); 

        $query=$conn->query("select userId from follower where companyId=$companyId");

        $userIds='';
        foreach ($query as $row) {
            $userIds=$userIds.','.$row['userId'];
        }
      
        $query = $conn->query("SELECT id FROM posts order by id desc")->fetch();
 
        $result["postId"]=$query['id'];
        $result["userIds"]=$userIds;

      

    }
    else{
        $result["success"]=false;
    }
      
   echo json_encode($result);
   $conn=null;
}

?>