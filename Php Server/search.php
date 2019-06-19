<?php


if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    require_once 'connect.php';


    $text = $_POST['search_text'];
    $result['search_list'] = array();


        $query = $conn->query("Select * from users where name LIKE '%$text%' or surname LIKE '%$text%'");

      
        if ($query) {
            $result['success'] = true;
            foreach ($query as $row) {
                $index["id"] = $row["id"];
                $index["name"] = $row["name"];
                $index["surname"] = $row["surname"];
                $index["image_url"] = $row["image_url"];

                array_push($result['search_list'], $index);
            }
        } else {
            $result['success'] = false;
        }
    


    echo json_encode($result);
    $conn = null;
}

?>