
<?php
try {
     $conn = new PDO("mysql:host=localhost;dbname=id9336347_company_db;", "id9336347_kemal", "karakartal78");
     $conn->query("SET CHARACTER SET utf8");
} catch ( PDOException $e ){
     print $e->getMessage();
}
?>