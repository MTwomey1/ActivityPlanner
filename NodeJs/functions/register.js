'use strict';

var neo4j = require('neo4j-driver').v1;
var driver = neo4j.driver("bolt://hobby-hncajieppajlgbkebcacggal.dbs.graphenedb.com:24786", neo4j.auth.basic("myapp", "b.K8YbBk5dbqUi.1NjWuVJWFXZvFFN6"));
var session = driver.session();

exports.registerUser = (user_username, user_firstname, user_lastname) =>

	new Promise((resolve, reject) => {
		
		session
		.run("CREATE (n:User { username: $username, firstname: $firstname, lastname: $lastname } ) RETURN n.username",
			{username: user_username, firstname: user_firstname, lastname: user_lastname})
		.then(function(result) {
        result.records.forEach(function(record) {
            console.log(record)
        });

        session.close();
		})
		.catch(function(error) {
			console.log("me "+ error);
		});
	});

