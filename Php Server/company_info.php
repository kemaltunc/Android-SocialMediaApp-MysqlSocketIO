 <?php


if ($_SERVER['REQUEST_METHOD'] =='POST'){

    require_once 'connect.php';
    $id =(int) $_POST['id'];   
    $type=$_POST["type"];
   

    $query=$conn->query("Select * FROM company where id=$id")->fetch(PDO::FETCH_ASSOC);
   
   
    if($query){
           
        $result['name'] = $query['company_name'];
        $result['sector'] = $query['sector'];      

        if($type=="typeone"){
            $myId=$_POST["myId"];
            $followControl=$conn->query("select * from follower where userId=$myId and companyId=$id")->fetch(PDO::FETCH_ASSOC);
            
            if($followControl){
                $result['followControl']=true;
            }
            else{
                $result['followControl']=false;
            }
            $follower=$conn->query("select * from follower where companyId=$id");
            $followerCount=$follower->rowCount();
            $post=$conn->query("select * from posts where company_id=$id");
            $postCount=$post->rowCount();
            $result["id"]=$id;
            $result['image_url'] = $query['image_url'];
            $result['status'] = $query['status'];
            $result["followingCount"]=$followerCount;
            $result["postCount"]=$postCount;           
            
        }else if($type=="typetwo"){
            $result['since'] = $query['since'];
            $result['description'] = $query['description'];
            $result['adress'] = $query['adress'];
        }
        else if($type=="typethree"){

            $result['user_list'] = array();
            $query=$conn->query("select userId from follower where companyId=$id");

            foreach($query as $row){
                $userId=$row['userId'];

                $user = $conn->query("Select * from users where id=$userId")->fetch(PDO::FETCH_ASSOC);
                $index["id"] = $user["id"];
                $index["name"] = $user["name"];
                $index["surname"] = $user["surname"];
                $index["image_url"] = $user["image_url"];

                array_push($result['user_list'], $index);


            }

        }
        else if($type=="typefour"){

            $result['user_list'] = array();
            $query=$conn->query("select * from users where companyId=$id");

            foreach($query as $row){
                $index["id"] = $row["id"];
                $index["name"] = $row["name"];
                $index["surname"] = $row["surname"];
                $index["image_url"] = $row["image_url"];
                array_push($result['user_list'], $index);
            }

        }


        $result['success'] = true;
      
    }else{
        $result['success'] = false;
    }
    echo json_encode($result);
    $conn=null;
}

?>