<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['amount'])){
		$data_missing[] = 'amount';
        }else{
                $amount = block_sql_attack($_POST['amount']);
                $int_amount = (int)$amount;
        }// end else
        
        if(empty($_POST['plan_id'])){
		$data_missing[] = 'plan_id';
        }else{
                $plan_id = block_sql_attack($_POST['plan_id']);
                $int_plan_id = (int)$plan_id;
        }// end else
        
        for($j = 0; $j < $int_amount; $j++){
                $username = block_sql_attack($_POST['username'.$j]);
                
                // make query
                $query = "INSERT IGNORE INTO plan_users (plan_id, username) VALUES (?,?)";
                
                // make prepare statement with query
                $stmt = mysqli_prepare($conn_db, $query);
                
                // combine the parameters
		mysqli_stmt_bind_param($stmt, "is", $int_plan_id, $username);
                
                // execute the query
                mysqli_stmt_execute($stmt);
                
        }                                             

        echo ("Users Inserted!");
        
        // close statement
        mysqli_stmt_close($stmt);
        

// close database connection
mysqli_close($conn_db);
	
?>