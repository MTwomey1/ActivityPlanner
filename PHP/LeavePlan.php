<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['username'])){
		$data_missing[] = 'username';
        }else{
                $username = block_sql_attack($_POST['username']);
        }// end else
        
        if(empty($_POST['planId'])){
		$data_missing[] = 'planId';
        }else{
                $planId = block_sql_attack($_POST['planId']);
                $int_planId = (int)$planId;
        }// end else
                      
        // make query
        $query = "DELETE FROM plan_users WHERE plan_id = ? AND username = ?";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        mysqli_stmt_bind_param($stmt, "is", $int_planId, $username);
                
        // execute the query
        mysqli_stmt_execute($stmt);
        
        // get number of affected rows
		$affected_rows = mysqli_stmt_affected_rows($stmt);
			
		if($affected_rows == 1){
			echo "Successful";
		}
        
        // close statement
        mysqli_stmt_close($stmt);
        


// close database connection
mysqli_close($conn_db);
	
?>