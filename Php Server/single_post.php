<?php


if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once 'connect.php';

    $id = (int)$_POST['id'];
    $postId=(int)$_POST['postId'];

    $query = $conn->query("Select users.image_url as userImage,company.image_url as companyImage,company.company_name,posts.* from users INNER JOIN posts ON users.id=posts.user_id INNER JOIN company ON posts.company_id=company.id where posts.id=$postId")->fetch(PDO::FETCH_ASSOC);


    if ($query) {
        $result['success'] = true;
        
            $result["userImage"] = $query["userImage"];
            $result["companyImage"] = $query["companyImage"];
            $result["companyName"] = $query["company_name"];
            $result["content"] = $query["content"];
            $result["date"] = $query["created_at"];
            $result["image_url"] = $query["image_url"];

        


          
    } else {
        $result['success'] = false;
    }

    echo json_encode($result);
    $conn = null;

}
?>

 