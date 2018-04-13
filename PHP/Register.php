<?php

	include('Functions.php'); // include functions
	
	$data_missing = array();
	
	if(empty($_POST['username'])){
		$data_missing[] = 'username';
		
	}else{
		
	        $username = block_sql_attack($_POST['username']);
	        $username_taken = check_username_used($username);
       
		if($username_taken == true){
          	  $data_missing[] = 'username_taken';
		}
       
	}// end else
		
	if(empty($_POST['password'])){
		$data_missing[] = 'password';
	}else{
		$password = block_sql_attack($_POST['password']);
	}// end else
		
	if(empty($_POST['email'])){
		$data_missing[] = 'email';
	}else{
		$email = block_sql_attack($_POST['email']);
		$email_taken = check_email_used($email);
		
		if($email_taken == true){
        	    $data_missing[] = 'email_taken';
		}
		
	}// end else
        
        if(empty($_POST['firstname'])){
		$data_missing[] = 'firstname';
	}else{
		$firstname = block_sql_attack($_POST['firstname']);
		
	}// end else
        
        
	if(empty($_POST['lastname'])){
		$data_missing[] = 'lastname';
	}else{
		$surname = block_sql_attack($_POST['lastname']);
		
	}// end else
	
	
	// if nothings missing connect to database
	if(empty($data_missing)){
		
		// last value is the strength starts at( 4 to 31 max) the higher the value the longer it takes 
		$password = encrypt_password($password, PASSWORD_BCRYPT , 10);
			
		// make query
		$query = "INSERT INTO user (username,password,email,firstname,lastname) VALUES (?,?,?,?,?)";
			
		// make prepare statement with query
		$stmt = mysqli_prepare($conn_db, $query);
			
		// combine the parameters
		mysqli_stmt_bind_param($stmt, "sssss", $username,$password,$email,$firstname,$surname);
			
		// execute the query
		mysqli_stmt_execute($stmt);
		
		// get number of affected rows
		$affected_rows = mysqli_stmt_affected_rows($stmt);
			
		if($affected_rows == 1){
			echo "Successful   !!!!Yoooooo!!!!";
		}
		
		// make profile folder for user
    		//if ( !is_dir("user_profile/" . $username) ) {
		//	mkdir("user_profile/" . $username, 0777, true);
		//}
		
		// close statement
		mysqli_stmt_close($stmt);	
		
	}else{
       
        if($data_missing[0] == username_taken){
            echo "username";
		}
		else if($data_missing[0] == email_taken){
			echo "email";
		}
        else{
            echo "missing data field";
        }
        
	}
	
	// close database connection
	mysqli_close($conn_db);
	
?>