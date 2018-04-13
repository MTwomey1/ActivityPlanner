<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['username'])){
		$data_missing[] = 'username';
        }else{
                $username = block_sql_attack($_POST['username']);
        }// end else
        
        if(empty($_POST['plan_id'])){
		$data_missing[] = 'plan_id';
        }else{
                $plan_id = block_sql_attack($_POST['plan_id']);
                $int_plan_id = (int)$plan_id;
        }// end else
                
        // make query
        $query = "UPDATE plan_users SET status = '1' WHERE plan_id = ? AND username = ?";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        mysqli_stmt_bind_param($stmt, "is", $plan_id, $username);
                
        // execute the query
        mysqli_stmt_execute($stmt);           

        // get number of affected rows
        $affected_rows = mysqli_stmt_affected_rows($stmt);
                
        if($affected_rows == 1){
                echo "Successful   !!!!Yoooooo!!!!";
        }
        
        // close statement
        mysqli_stmt_close($stmt);
        


// close database connection
mysqli_close($conn_db);
	
?>