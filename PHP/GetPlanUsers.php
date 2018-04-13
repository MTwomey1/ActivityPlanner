<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['plan_id'])){
		$data_missing[] = 'plan_id';
        }else{
                $plan_id = block_sql_attack($_POST['plan_id']);
                $int_plan_id = (int)$plan_id;
        }// end else
                
        // make query
        $query = "SELECT username, status FROM plan_users WHERE plan_id = ?";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        mysqli_stmt_bind_param($stmt, "i", $int_plan_id);
                
        // execute the query
        mysqli_stmt_execute($stmt);
        
        mysqli_stmt_store_result($stmt);
        mysqli_stmt_bind_result($stmt, $username, $status);
                
        $response = array();
        
        $i = 0;
        
        while(mysqli_stmt_fetch($stmt)) {
                $response[username . $i] = $username;
                $response[status . $i] = $status;
                
                $i++;
                
        }

        echo json_encode($response);
        
        // close statement
        mysqli_stmt_close($stmt);
        


// close database connection
mysqli_close($conn_db);
	
?>