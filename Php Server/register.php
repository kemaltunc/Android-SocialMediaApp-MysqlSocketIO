<?php

if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';

  
    $name = $_POST['name'];
    $surname = $_POST['surname'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $photo=$_POST['photo'];

    $photo_name=md5(uniqid()).".jpeg";
    $path="uploads/images/profile_images/$photo_name";

   

    $password = password_hash($password, PASSWORD_DEFAULT);

  
    $query=$conn->query("Select * FROM users where email='$email'")->fetch(PDO::FETCH_ASSOC);

    if($query)
        $result["control"]=true;            
    else
    {

    $result["control"]=false;    
    $query=$conn->prepare("INSERT INTO users SET
    name=?,
    surname=?,
    email=?,
    password=?,
    image_url=?");
    
    $insert=$query->execute(array(
      $name,$surname,$email,$password,$photo_name
    ));

    if($insert){

        $query=$conn->query("Select id FROM users where email='$email'")->fetch(PDO::FETCH_ASSOC);
        $result["myId"]=$query["id"];
        $result["success"] = true; 
        $decoded=base64_decode($photo);
        file_put_contents($path,$decoded);   
    }

    else
        $result["success"] = false; 
   
   }
    
   echo json_encode($result);
   $conn=null;
}
?>