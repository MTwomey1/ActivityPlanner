<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['mUsername'])){
		$data_missing[] = 'mUsername';
        }else{
                $mUsername = block_sql_attack($_POST['mUsername']);
        }// end else
        
        if(empty($_POST['Username2'])){
		$data_missing[] = 'Username2';
        }else{
                $Username2 = block_sql_attack($_POST['Username2']);
        }// end else
                
        // make query
        $query = "DELETE FROM friends WHERE friend_one = ? AND friend_two = ?";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        mysqli_stmt_bind_param($stmt, "ss", $Username2,$mUsername);
                
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