<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['username'])){
		$data_missing[] = 'username';
        }else{
                $username = block_sql_attack($_POST['username']);
        }// end else
        
        if(empty($_POST['mUsername'])){
		$data_missing[] = 'mUsername';
        }else{
                $mUsername = block_sql_attack($_POST['mUsername']);
        }// end else
                
        // make query
        $query = "UPDATE friends SET status = '1' WHERE friend_one = ? AND friend_two = ?";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        mysqli_stmt_bind_param($stmt, "ss", $username,$mUsername);
                
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