<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    require_once 'connect.php';

    $conversationId =$_POST["conversationId"];
    $type = $_POST["type"];

    if ($type == "load") {

        $result['message_list'] = array();
        $query = $conn->query("SELECT content,name,surname,senderId,image_url as image,companies_messages.created_at as date FROM companies_messages INNER JOIN users ON companies_messages.senderId=users.id WHERE companies_messages.companyId='$conversationId'");

        foreach ($query as $row) {
            $index["name"] = $row["name"];
            $index["surname"] = $row["surname"];
            $index["senderId"] = $row["senderId"];
            $index["date"] = $row["date"];
            $index["image"] = $row["image"];
            $index["content"] = $row["content"];
           
            array_push($result['message_list'], $index);
        }

        if ($query)
            $result['success'] = true;
        else
            $result['success'] = false;
    } else if ($type == "write") {
        $senderId = (int)$_POST["senderId"];
        $message = $_POST["message"];
        $query = $conn->prepare("INSERT INTO companies_messages set 
        senderId=?,
        companyId=?,
        content=?");

        $insert = $query->execute(array($senderId, $conversationId, $message));

        if ($insert)
            $result['success'] = true;
        else
            $result['success'] = false;
    }


    echo json_encode($result);
    $conn = null;
}
