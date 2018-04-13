<?php

	include('Functions.php'); // include functions
	
	if(empty($_POST['username']) || empty($_POST['password'])){ // check if fields are empty
		
		echo "failed";
	}
	else{
        
        
		$username = block_sql_attack($_POST['username']);
		$password = block_sql_attack($_POST['password']);
                
		$statement = mysqli_prepare($conn_db,"SELECT password FROM user WHERE username= ? LIMIT 1");
		mysqli_stmt_bind_param($statement, "s", $username);
		mysqli_stmt_execute($statement);
		
		mysqli_stmt_store_result($statement);
		mysqli_stmt_bind_result($statement,$pass);
		
		// get hashed password row from database
		while(mysqli_stmt_fetch($statement)){
			$hash =  $pass;
		}
		
		if(!isset($hash)){
			$hash = "wrong";
		}
                
		
		mysqli_stmt_close($statement);
		
		// if their password match let them in
		if(password_verify($password, $hash)){
                
			$query = "SELECT * FROM user WHERE username= '$username' LIMIT 1";
			
			$response = @mysqli_query($conn_db, $query);

			$user_data = array();
			
			// get hashed password row from database
			while($row = mysqli_fetch_array($response)){
				$user_data[username] =  $row['username'];
				$user_data[password] = $password;
				$user_data[email] = $row['email'];
                                $user_data[firstname] = $row['firstname'];
				$user_data[lastname] = $row['lastname'];                            

			}
			
			echo json_encode($user_data);

		}
                else{
                        echo "failed";
                }
	
	}
	
	// close database connection
	mysqli_close($conn_db);
	
?>