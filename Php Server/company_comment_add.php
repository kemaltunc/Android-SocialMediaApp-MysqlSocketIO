<?php


if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once 'connect.php';

    $userId = (int)$_POST['userId'];
    $companyId = (int)$_POST['companyId'];
    $type=$_POST['type'];


    if($type=="write"){
        $content=$_POST['content'];
        $point=(float)$_POST['point'];

        $query=$conn->prepare("INSERT INTO company_comment Set
        userId=?,
        companyId=?,
        comment=?,
        point=?");
        

        $insert=$query->execute(array( $userId,$companyId,$content,$point));

        if($insert){
            $result["success"]=true;
            
        }
        else{
            $result["success"]=false;
        }
    }
    else if($type=="read"){
        $result['comment_list'] = array();
        $query = $conn->query("SELECT * FROM company_comment INNER JOIN users ON company_comment.userId=users.id where company_comment.companyId=$companyId order by company_comment.id desc");


        if($query){
            $result['success'] = true;
            foreach ($query as $row) {
              
             
                $index["name"] = $row["name"];
                $index["surname"] = $row["surname"];
                $index["userImage"] = $row["image_url"];
                $index["content"] = $row["comment"];
                $index["rating"] = $row["point"];

                array_push($result['comment_list'], $index);
            }
        }
        else{
            $result['success'] = false;
        }
    }
    echo json_encode($result);
    $conn = null;

}
?>
