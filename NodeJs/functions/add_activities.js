'use strict';

var neo4j = require('neo4j-driver').v1;
var driver = neo4j.driver("bolt://hobby-hncajieppajlgbkebcacggal.dbs.graphenedb.com:24786", neo4j.auth.basic("myapp", "b.K8YbBk5dbqUi.1NjWuVJWFXZvFFN6"));
var session = driver.session();

exports.addActivities = (user_username, output) =>
	
	new Promise((resolve, reject) => {
		console.log(user_username+" "+output)
		
		session
			.run('MATCH (u:User {username: $username}) ' +
				'MATCH (u)-[r:LIKES]->(:Activity) ' +
			'DELETE r', {'username': user_username} )
			.then(function(result) {
			result.records.forEach(function(record) {
				console.log(record)
			});
				session.close();
			})
			.catch(function(error) {
				console.log("me "+ error);
			});
		
		for (var i = 0; i < output.length; i++){
			session
			.run('MATCH (u:User {username: $username}) ' +
				'MATCH (n:Activity {name: $activities}) ' +
			'CREATE UNIQUE (u)-[:LIKES]->(n)', {'username': user_username, 'activities': output[i]})
			.then(function(result) {
			result.records.forEach(function(record) {
				console.log(record)
			});
			resolve("yo");
			session.close();
				})
			.catch(function(error) {
				console.log("me "+ error);
			});
		}
	});