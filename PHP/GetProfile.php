<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['getprofile'])){
		$data_missing[] = 'getprofile';
        }else{
                $getprofile = block_sql_attack($_POST['getprofile']);
        }// end else
                
        // make query
        $query = "SELECT username, firstname, lastname, public FROM user WHERE username = ?";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        mysqli_stmt_bind_param($stmt, "s", $getprofile);
                
        // execute the query
        mysqli_stmt_execute($stmt);
        
        mysqli_stmt_store_result($stmt);
        mysqli_stmt_bind_result($stmt, $username, $firstname, $lastname, $public);
                
        $response = array();
        
        //$i = 0;
        
        while(mysqli_stmt_fetch($stmt)) {
                $response[username] = $username;
                $response[firstname] = $firstname;
                $response[lastname] = $lastname;
                $response[privacy] = $public;
                
                //$i++;
                
        }

        echo json_encode($response);
        
        // close statement
        mysqli_stmt_close($stmt);
        


// close database connection
mysqli_close($conn_db);
	
?>