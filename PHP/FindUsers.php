<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['finduser'])){
		$data_missing[] = 'finduser';
        }else{
                $finduser = block_sql_attack($_POST['finduser']);
        }// end else
                
        // make query
        $query = "SELECT username FROM user WHERE username LIKE '%{$finduser}%'";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        //mysqli_stmt_bind_param($stmt, "s", $username);
                
        // execute the query
        mysqli_stmt_execute($stmt);
        
        mysqli_stmt_store_result($stmt);
        mysqli_stmt_bind_result($stmt, $username);
                
        $response = array();
        
        $i = 0;
        
        while(mysqli_stmt_fetch($stmt)) {
                $response[username . $i] = $username;
                
                $i++;
                
        }

        echo json_encode($response);
        
        // close statement
        mysqli_stmt_close($stmt);
        


// close database connection
mysqli_close($conn_db);
	
?>