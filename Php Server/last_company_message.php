<?php


if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    require_once 'connect.php';

    $companyId = (int)$_POST["companyId"];
    $type = $_POST["type"];

    if ($type == "MyCompany") {
        $query = $conn->query("select company_message.id as messageId, senderId,content, company_message.created_at as date,users.id as userId, name,surname,image_url as image from company_message INNER JOIN users ON company_message.senderId=users.id where company_message.companyId=$companyId ORDER BY company_message.id DESC LIMIT 1")->fetch(PDO::FETCH_ASSOC);
        if ($query) {
            $result['success'] = true;

            $result["name"] = $query["name"];
            $result["surname"] = $query["surname"];
            $result["image"] = $query["image"];
            $result['content'] = $query["content"];
            $result["date"] = $query["date"];
        } else {
            $result['success'] = false;
        }
    } else if ($type == "OtherCompany") {
        $result['last_message'] = array();
        $companyId = $_POST["companyId"];
        $query = $conn->query("SELECT DISTINCT(companyId) FROM companies_messages WHERE companyId LIKE '%$companyId%'");
        if ($query) {
            $result['success'] = true;
            foreach ($query as $row) {
                $conversationId = $row["companyId"];

                $splitId = (int)str_replace($companyId, '', $conversationId);

                $message = $conn->query("SELECT content,companies_messages.created_at as date,name,surname FROM companies_messages INNER JOIN users ON companies_messages.senderId=users.id where companies_messages.companyId='$conversationId' ORDER BY companies_messages.id DESC LIMIT 1")->fetch(PDO::FETCH_ASSOC);

                $index["companyId"] = $splitId;
                $index["conversationId"]=$conversationId;
                $index["username"] = $message["name"] . " " . $message["surname"];
                $index["content"] = $message["content"];
                $index["date"] = $message["date"];

                $company = $conn->query("SELECT company_name as companyname,image_url as companyImage from company where id=$splitId")->fetch(PDO::FETCH_ASSOC);

                $index["companyname"] = $company["companyname"];
                $index["image"] = $company["companyImage"];

                array_push($result['last_message'], $index);
            }
        }
    }


    echo json_encode($result);
    $conn = null;
}
