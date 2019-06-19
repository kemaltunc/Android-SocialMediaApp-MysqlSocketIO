<?php


if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    require_once 'connect.php';

    $myId = (int)$_POST["myId"];
    $otherId = (int)$_POST["otherId"];
    $type = $_POST["type"];

    if ($type == "load") {
        $query = $conn->query("Select * from private_message where senderId=$myId and receiverId=$otherId or senderId=$otherId and receiverId=$myId");


        $result['message_list'] = array();
        if ($query) {
            $result['success'] = true;

            $count = $query->rowCount();
            if ($count == 0) {
                $result["count"] = false;
            } else {
                $result["count"] = true;
                foreach ($query as $row) {
                    $index["id"] = $row["id"];
                    $index["senderId"] = $row["senderId"];
                    $index["receiverId"] = $row["receiverId"];
                    $index["conversationId"] = $row["conversationId"];
                    $index["message"] = $row["message"];
                    $index["date"] = $row["created_at"];
                    $index["status"] = $row["status"];
                    $index["messageType"] = $row["message_type"];

                    array_push($result['message_list'], $index);
                }
            }

        } else {
            $result['success'] = false;
        }

    }
    if ($type == "write") {
        $conversationId = $_POST["conversationId"];
        $message = $_POST["message"];
        $status = $_POST["status"];
        $messageType = $_POST["messageType"];


        if ($messageType == "image") {
            $photo_name = md5(uniqid());
            $path = "uploads/images/message_images/$photo_name.jpeg";
            $decoded = base64_decode($message);
            file_put_contents($path, $decoded);
            $message = $photo_name . ".jpeg";

            $result["messageType"] = "image";
        } else {
            $result["messageType"] = "text";
        }


        $query = $conn->prepare("INSERT INTO private_message set 
        senderId=?,
        receiverId=?,
        conversationId=?,
        message=?,
        status=?,
        message_type=?");

        $insert = $query->execute(array($myId, $otherId, $conversationId, $message, $status, $messageType));

        if ($insert) {
            $result['success'] = true;
        } else {
            $result['success'] = false;
        }
    }


    echo json_encode($result);
    $conn = null;
}

?>