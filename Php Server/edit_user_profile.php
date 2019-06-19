<?php 

if ($_SERVER['REQUEST_METHOD'] =='POST'){
    require_once 'connect.php';
   
 
    $id =(int) $_POST['id'];
    $column=$_POST['column'];
    $value=$_POST['value'];

    if($column=="image_url"){        
        $query=$conn->query("Select * FROM users where id=$id")->fetch(PDO::FETCH_ASSOC);
            $photo_name= $query['image_url'];
            $path="uploads/images/profile_images/$photo_name";
            $finalPath= $serverAdress.$path;
            $decoded=base64_decode($value);
            file_put_contents($path,$decoded);
            
            $value=$finalPath;
          
    } 
    $query=$conn->prepare("Update users set
    $column=:new_value
    where id=:id");

    $update=$query->execute(array(
    "new_value"=>$value,
    "id"=>$id));

    if($update)
        $result["control"]=true;
    else
       $result["control"]=false;
        
   
    echo json_encode($result);
    $conn=null;

}

?>