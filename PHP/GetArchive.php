<?php

include('Functions.php'); // include functions
        
        if(empty($_POST['username'])){
		$data_missing[] = 'username';
        }else{
                $username = block_sql_attack($_POST['username']);
        }// end else
                
        // make query
        $query = "SELECT plan_id, username, activity, date, location FROM plans WHERE username = ? AND completed = '1' UNION SELECT plans.plan_id, plans.username, plans.activity, plans.date, plans.location FROM plan_users JOIN plans on plans.plan_id = plan_users.plan_id WHERE plan_users.username = ? AND plan_users.status = '1' AND plans.completed = '1'";
                
        // make prepare statement with query
        $stmt = mysqli_prepare($conn_db, $query);
                
        // combine the parameters
        mysqli_stmt_bind_param($stmt, "ss", $username, $username);
                
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