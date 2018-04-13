<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['planId'])){
		$data_missing[] = 'planId';
        }else{
                $planId = block_sql_attack($_POST['planId']);
                $int_planId = (int)$planId;
        }// end else
                      
        // make query
        $query = "DELETE FROM plans WHERE plan_id = ?";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        mysqli_stmt_bind_param($stmt, "i", $int_planId);
                
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