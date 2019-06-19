<?php


if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    require_once 'connect.php';

    $id = (int)$_POST["myId"];
    $query = $conn->query("Select DISTINCT (conversationId) from private_message where senderId=$id or receiverId=$id");

    $result['last_message'] = array();
    if ($query) {
        $result['success'] = true;

        foreach ($query as $row) {
            $conversationId = $row["conversationId"];

            $lastMessage = $conn->query("SELECT * from private_message where conversationId='$conversationId' ORDER by id DESC LIMIT 1");

            foreach ($lastMessage as $last) {

                $index["message"] = $last["message"];
                $index["date"] = $last["created_at"];
                $index["senderId"] = $last["senderId"];
                $index["receiverId"] = $last["receiverId"];
                $index["conversationId"] = $last["conversationId"];
                $index["messageType"] = $last["message_type"];


                if ($id == $last["senderId"]) {
                    $otherUserId = (int)$last["receiverId"];
                } else if ($id == $last["receiverId"]) {
                    $otherUserId = (int)$last["senderId"];
                }
                $index["toUserId"] = $otherUserId;

                $userInfo = $conn->query("SELECT name,surname,image_url FROM users where id=$otherUserId")->fetch(PDO::FETCH_ASSOC);
                $index["name"] = $userInfo["name"];
                $index["surname"] = $userInfo["surname"];
                $index["image_url"] = $userInfo["image_url"];


                array_push($result['last_message'], $index);
            }
        }
    } else {
        $result['success'] = false;
    }


    echo json_encode($result);
    $conn = null;
}

?>