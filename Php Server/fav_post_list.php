<?php

if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';

    $id=(int)$_POST["id"];

    $query=$conn->query("select users.image_url as userImage,company.image_url as companyImage,company.company_name,posts.* from favourite_post INNER JOIN posts On favourite_post.postId=posts.id INNER JOIN users ON posts.user_id=users.id INNER JOIN company ON users.companyId=company.id where favourite_post.userId=$id");
   
   
    $result['post_list'] = array();
    if($query){
        $result['success'] = true;
        foreach ($query as $row) { 
            $index["userImage"] = $row["userImage"];
            $index["companyImage"] = $row["companyImage"];
            $index["companyName"] = $row["company_name"];
            $index["postId"] = $row["id"];
            $index["content"] = $row["content"];
            $index["date"] = $row["created_at"];
            $index["image_url"] = $row["image_url"];

            $postId = $row["id"];
            $comment = $conn->query("Select id from post_comment where postId=$postId");
            $commentCount = $comment->rowCount();
            $index["commentCount"] = $commentCount;

            $like = $conn->query("Select postId from post_like where postId in($postId)");
            $likeCount = $like->rowCount();
            $index["likeCount"] = $likeCount;

            $mLike = $conn->query("Select postId from post_like where postId in($postId) and userId=$id");
            if ($mLike->rowCount() > 0) {
                $index["likeControl"] = true;
            } else {
                $index["likeControl"] = false;
            }
            $mFav = $conn->query("Select postId from favourite_post where postId in($postId) and userId=$id");

            if ($mFav->rowCount() > 0) {
                $index["favControl"] = true;
            } else {
                $index["favControl"] = false;
            }

            array_push($result['post_list'], $index);

        }
    }

    echo json_encode($result);
    $conn=null;
 }
 
 ?>
 
 