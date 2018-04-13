<?php

	// connect to database
	require_once('Connection.php');

	function block_sql_attack($string){
		
		// To protect MySQL injection for Security purpose
		$string = trim($string);
		$string = stripslashes($string);
		$string = htmlspecialchars($string);
		
		return $string;
		
	}// end block_sql_attack
	
	
	
	// encrypt users passwords
	function encrypt_password($password,$algorithm,$cost){
	
		$hash = password_hash($password, $algorithm , array("cost" => "$cost"));
		
		if(password_verify($password, $hash)){
			return $hash;
		}
		else{
			return $password;
		}
		
	}// end encrypt_password
	
	
	
	function validate_phone_number($string){
		
		if(is_numeric($string) && strlen($string) <= 10){
			return 1;
		}
		else{
			return 0;
		}
	
	}// end validate_number
	
	
	
	function confirm_password($string_1, $string_2){
		
		if($string_1 === $string_2 && strlen($string_1) <= 6){
			return 1;
		}
		else{
			return 0;
		}
	
	}// end validate_number
	
	
	
	// check if username is same
	function check_username_used($string){
	
		// make database connection
		$conn_db = mysqli_connect(DB_HOST , DB_USER , DB_PASSWORD , DB_NAME);
		
		$query = "SELECT * FROM user WHERE username = '$string'";
		
		// check connection
		if(mysqli_connect_errno($conn_db)){
			echo "Failed to connect to MySQL: " . mysqli_connect_error();
		}// end if
		
		$response = mysqli_query($conn_db, $query);
		
		$num_rows = $response->num_rows;
		
		// close connection to database
		mysqli_close($conn_db);
		unset($conn_db);
		
		if($num_rows > 0 )
		{
			return true; // a user has the same username, not good
		}
		else{
			return false; // no user with same username, all good
		}
	
	}// end 
        
        
        
        // check if email is same
	function check_email_used($string){
	
		// make database connection
		$conn_db = mysqli_connect(DB_HOST , DB_USER , DB_PASSWORD , DB_NAME);
		
		$query = "SELECT * FROM user WHERE email = '$string'";
		
		// check connection
		if(mysqli_connect_errno($conn_db)){
			echo "Failed to connect to MySQL: " . mysqli_connect_error();
		}// end if
		
		$response = mysqli_query($conn_db, $query);
		
		$num_rows = $response->num_rows;
		
		// close connection to database
		mysqli_close($conn_db);
		unset($conn_db);
		
		if($num_rows > 0 )
		{
			return true; // a user has the same username, not good
		}
		else{
			return false; // no user with same username, all good
		}
	
	}// end 
	
?>