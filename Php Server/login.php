<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once 'connect.php';


    $email = $_POST['email'];
    $password = $_POST['password'];
    $query = $conn->query("Select * FROM users where email='$email'")->fetch(PDO::FETCH_ASSOC);

    if ($query) {

        if (password_verify($password, $query['password'])) {
            $result['success'] = true;
            $result['id'] = $query['id'];
            $result['name'] = $query['name'];
            $result['surname'] = $query['surname'];
            $result['userImage'] = $query['image_url'];
            $result['companyId'] = $query['companyId'];

            $companyId = (int)$query['companyId'];

            $query = $conn->query("Select company_name,image_url FROM company where id=$companyId")->fetch(PDO::FETCH_ASSOC);
            $result['companyName'] = $query['company_name'];
            $result['companyImage'] = $query['image_url'];


        } else
            $result['success'] = false;
    } else {
        $result['success'] = false;
    }

    echo json_encode($result);
    $conn = null;


}

?>