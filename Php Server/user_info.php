<?php


if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';
    $id =(int) $_POST['id'];   
    $type=$_POST["type"];
    $query=$conn->query("Select * FROM users where id=$id")->fetch(PDO::FETCH_ASSOC);
   
    $result['user_info'] = array();
    if($query){
           
        $result['name'] = $query['name'];
        $result['surname'] = $query['surname'];      

        if($type=="typeone"){
            $follower=$conn->query("select * from company INNER JOIN follower on company.id=follower.companyId where follower.userId=$id");
            $followerCount=$follower->rowCount();

            $post=$conn->query("select id from posts where user_id=$id");
            $postCount=$post->rowCount();

            $result['image_url'] = $query['image_url'];
            $result['status'] = $query['status'];
            $result["followingCount"]=$followerCount;
            $result["postCount"]=$postCount;           
            
        }else{
            $result['birthday']=$query['birthday'];
            $result['phone']=$query['phone'];
            $result['email_adress']=$query['email_adress'];
            $result['website_adress']=$query['website_adress'];
            $result['education']=$query['education'];
            $result['unvan']=$query['unvan'];


        }
    

        $result['success'] = true;
      
    }else{
        $result['success'] = false;
    }
    echo json_encode($result);
    $conn=null;
}

?>