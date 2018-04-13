<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['username'])){
		$data_missing[] = 'username';
        }else{
                $username = block_sql_attack($_POST['username']);
        }// end else
                
        // make query
        $query = "SELECT plans.plan_id, plans.username, activity, date, location FROM plan_users JOIN plans ON plan_users.plan_id = plans.plan_id WHERE plan_users.username = ? AND plan_users.status IS NULL";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        mysqli_stmt_bind_param($stmt, "s", $username);
                
        // execute the query
        mysqli_stmt_execute($stmt);
        
        mysqli_stmt_store_result($stmt);
        mysqli_stmt_bind_result($stmt, $plan_id, $username, $activity, $date, $location);
                
        $response = array();
        
        $i = 0;
        
        while(mysqli_stmt_fetch($stmt)) {
                $response[plan_id . $i] = $plan_id;
                $response[username . $i] = $username;
                $response[activity . $i] = $activity;
                $response[date . $i] = $date;
                $response[location . $i] = $location;
                
                $i++;
                
        }

        echo json_encode($response);
        
        // close statement
        mysqli_stmt_close($stmt);
        


// close database connection
mysqli_close($conn_db);
	
?>