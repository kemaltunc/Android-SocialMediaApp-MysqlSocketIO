<?php

if ($_SERVER['REQUEST_METHOD'] =='POST'){
    require_once 'connect.php';
    $data=rtrim($_POST["data"],",");

    $result['company_list'] = array();
    $query = $conn->query("select * from company  where sector IN($data)");

    if($query){
        $result['success'] = true;
        foreach ($query as $row) {

            $id=$row["id"];
            $point=0;
            $comment_point=$conn->query("select sum(point) * 0.5 as point from company_comment where companyId=$id")->fetch(PDO::FETCH_ASSOC);
            $post_point=$conn->query("select count(company_id) * 0.3 as point from posts where company_id=$id")->fetch(PDO::FETCH_ASSOC);
            $follow_point=$conn->query("select count(companyId) * 0.2 as point from follower where companyId=$id")->fetch(PDO::FETCH_ASSOC);

            if($comment_point)
             $point+=$comment_point["point"];
             
            if($post_point) 
             $point+=$post_point["point"];

            if($follow_point) 
             $point+=$follow_point["point"]; 

            $index["id"] = $id;
            $index["name"] = $row["company_name"];
            $index["sector"]=$row["sector"];
            $index["image"] = $row["image_url"];
            $index["point"] = $point;
            array_push($result['company_list'], $index);
          


        }
    }

    echo json_encode($result);
    $conn=null;
 }
 
 ?>
 