<?php

if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';
    require_once 'server_adress.php';
    
    $userId=$_POST['userId'];
    $name = $_POST['name'];
    $since = $_POST['since'];
    $sector = $_POST['sector'];
    $adress = $_POST['adress'];
    $city=$_POST['city'];
    $ilce=$_POST['ilce'];
    $description=$_POST['description'];
    $photo=$_POST['photo'];

    $full_adress=$adress." ".$ilce." ".$city;
    
    $photo_name=md5(uniqid()).".jpeg";
    $path="uploads/images/company_images/$photo_name";

    $query=$conn->prepare("INSERT INTO company Set
    company_name=?,
    since=?,
    sector=?,
    adress=?,
    description=?,
    image_url=?");
    
    $insert=$query->execute(array( $name,$since,$sector,$full_adress,$description,$photo_name));
      
    if($insert){     

        $query=$conn->query("Select id FROM company where image_url='$finalPath'")->fetch(PDO::FETCH_ASSOC);
        $companyId=$query["id"];

        $query=$conn->prepare("Update users set
        companyId=:new_companyId,
        yetki=:new_yetki        
        where id=:id");

        $update=$query->execute(array(
        "new_companyId"=>$companyId,
        "new_yetki"=>"Kurucu",
        "id"=>$userId));

        $result["id"]=$companyId;
        $result["companyName"]=$name;
        $result["image_url"]=$finalPath;
        $decoded=base64_decode($photo);
        file_put_contents($path,$decoded);  
        $result["control"]=true;

    }else{
        $result["control"]=false;
    }
     
   echo json_encode($result);
   $conn=null;
}

?>

