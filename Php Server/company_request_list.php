<?php
if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';
 
    $companyId=(int)$_POST["companyId"];
    

    $query=$conn->query("Select company_join.id as joinId,users.id as userId, users.name,users.surname,users.image_url as image from company_join INNER JOIN users ON company_join.userId=users.id where company_join.companyId=$companyId");

    $result['user_list'] = array();
    if($query){
        $result["success"]=true;

        foreach($query as $row){
            $index["joinId"]=$row["joinId"];
            $index["userId"]=$row["userId"];
            $index["name"]=$row["name"];
            $index["surname"]=$row["surname"];
            $index["image"]=$row["image"];

            array_push($result['user_list'], $index);

        }


    }
    else{
        $result["success"]=false;
    }


      
    echo json_encode($result);
    $conn=null;
}

?>