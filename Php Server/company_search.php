<?php


if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    require_once 'connect.php';
    $text = $_POST['search_text'];

    $result['search_list'] = array();

    $query = $conn->query("Select * from company where company_name LIKE '%$text%'");
        
    if ($query) {
        $result['success'] = true;
        foreach ($query as $row) {
            $index["id"] = $row["id"];
            $index["name"] = $row["company_name"];
            $index["sector"] = $row["sector"];
            $index["image_url"] = $row["image_url"];

            array_push($result['search_list'], $index);
        }
    } else {
        $result['success'] = false;
    }
    
    echo json_encode($result);
    $conn = null;

}