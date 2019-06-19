<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    require_once 'connect.php';

    $companyId = (int)$_POST["companyId"];
    $type = $_POST["type"];

    if ($type == "load") {

        $result['message_list'] = array();
        $query = $conn->query("select company_message.id as messageId, senderId,content,company_message.created_at as date, users.id as userId, name,surname,image_url as image from company_message INNER JOIN users ON company_message.senderId=users.id where company_message.companyId=$companyId");

        foreach ($query as $row) {
            $index["messageId"] = $row["messageId"];
            $index["senderId"] = $row["senderId"];
            $index["content"] = $row["content"];
            $index["date"] = $row["date"];
            $index["userId"] = $row["userId"];
            $index["name"] = $row["name"];
            $index["surname"] = $row["surname"];
            $index["image"] = $row["image"];

            array_push($result['message_list'], $index);
        }

        if ($query)
            $result['success'] = true;
        else
            $result['success'] = false;
    } else if ($type == "write") {
        $senderId = (int)$_POST["senderId"];
        $message = $_POST["message"];
        $query = $conn->prepare("INSERT INTO company_message set 
        senderId=?,
        companyId=?,
        content=?");

        $insert = $query->execute(array($senderId, $companyId, $message));

        if ($insert)
            $result['success'] = true;
        else
            $result['success'] = false;
    }


    echo json_encode($result);
    $conn = null;
}
