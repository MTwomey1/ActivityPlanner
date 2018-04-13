'use strict';

var neo4j = require('neo4j-driver').v1;
var driver = neo4j.driver("bolt://hobby-hncajieppajlgbkebcacggal.dbs.graphenedb.com:24786", neo4j.auth.basic("myapp", "b.K8YbBk5dbqUi.1NjWuVJWFXZvFFN6"));
var session = driver.session();

exports.addFriend = (user_username1, user_username2) =>
	
	new Promise((resolve, reject) => {
		console.log(user_username1+user_username2)
		
		session
		.run('MATCH (u:User {username: $username1}) ' +
			'MATCH (n:User {username: $username2}) ' +
			'CREATE (u)-[:FRIEND_OF]->(n)', {'username1': user_username1, 'username2': user_username2})
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