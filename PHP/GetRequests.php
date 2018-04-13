<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['mUsername'])){
		$data_missing[] = 'mUsername';
        }else{
                $mUsername = block_sql_attack($_POST['mUsername']);
        }// end else
                
        // make query
        $query = "SELECT friend_one FROM friends WHERE friend_two = ? AND status = '0'";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        mysqli_stmt_bind_param($stmt, "s", $mUsername);
                
        // execute the query
        mysqli_stmt_execute($stmt);
        
        mysqli_stmt_store_result($stmt);
        mysqli_stmt_bind_result($stmt, $friend_one);
                
        $response = array();
        
        $i = 0;
        
        while(mysqli_stmt_fetch($stmt)) {
                $response[friend_one . $i] = $friend_one;
                
                $i++;
                
        }

        echo json_encode($response);
        
        // close statement
        mysqli_stmt_close($stmt);
        


// close database connection
mysqli_close($conn_db);
	
?>