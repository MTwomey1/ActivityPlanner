<?php

	include('Functions.php'); // include functions
	
	$data_missing = array();
        
        if(empty($_POST['username'])){
		$data_missing[] = 'username';
        }else{
                $username = block_sql_attack($_POST['username']);
        }// end else
	
	if(empty($_POST['activity'])){
		$data_missing[] = 'activity';
        }else{
                $activity = block_sql_attack($_POST['activity']);
        }// end else
		
	if(empty($_POST['date'])){
		$data_missing[] = 'date';
	}else{
                $date = block_sql_attack($_POST['date']);
        }// end else
		
	if(empty($_POST['location'])){
		$data_missing[] = 'location';
	}else{
                $location = block_sql_attack($_POST['location']);
        }// end else
        
        if(empty($_POST['choice'])){
		$data_missing[] = 'choice';
	}else{
                $choice = block_sql_attack($_POST['choice']);
                $int_choice = (int) $choice;
        }// end else
        	
	
	// if nothings missing connect to database
	if(empty($data_missing)){		
			
		// make query
		$query = "INSERT INTO plans (username,activity,date,location,public) VALUES (?,?,?,?,?)";
			
		// make prepare statement with query
		$stmt = mysqli_prepare($conn_db, $query);
			
		// combine the parameters
		mysqli_stmt_bind_param($stmt, "ssssi", $username,$activity,$date,$location,$int_choice);
			
		// execute the query
		mysqli_stmt_execute($stmt);
		
		// get number of affected rows
		$affected_rows = mysqli_stmt_affected_rows($stmt);
			
		if($affected_rows == 1){
                        $last_id = $stmt->insert_id;
			echo("poop "+$last_id);
		}
		
		
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