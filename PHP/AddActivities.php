<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['username'])){
		$data_missing[] = 'username';
        }else{
                $username = block_sql_attack($_POST['username']);
        }// end else
        
        if(empty($_POST['amount'])){
		$data_missing[] = 'amount';
        }else{
                $amount = block_sql_attack($_POST['amount']);
                $int_amount = (int)$amount;
        }// end else
        
        for($j = 0; $j < $int_amount; $j++){
                $activity = block_sql_attack($_POST['activity+$j']);
                // make query
                $query = "INSERT INTO activities (username, activity) VALUES (?,?)";
                
                // make prepare statement with query
                $stmt = mysqli_prepare($conn_db, $query);
                
                // combine the parameters
		mysqli_stmt_bind_param($stmt, "ss", $username,$activity);
                
                // execute the query
                mysqli_stmt_execute($stmt);
        }                                             

        echo ("Activities Inserted");
        
        // close statement
        mysqli_stmt_close($stmt);
        


// close database connection
mysqli_close($conn_db);
	
?>