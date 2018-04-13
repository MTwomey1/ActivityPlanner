'use strict';

var neo4j = require('neo4j-driver').v1;
var driver = neo4j.driver("bolt://hobby-hncajieppajlgbkebcacggal.dbs.graphenedb.com:24786", neo4j.auth.basic("myapp", "b.K8YbBk5dbqUi.1NjWuVJWFXZvFFN6"));
var session = driver.session();

exports.checkFriends = (user_username1, user_username2) =>
	
	new Promise((resolve, reject) => {
		
		session
		.run('MATCH (u:User {username: $username1}) ' +
			'MATCH (n:User {username: $username2}) ' +
			'RETURN EXISTS( (u)-[:FRIEND_OF]-(n) ) AS are_friends', {'username1': user_username1, 'username2': user_username2})
		.then(function(result) {
			var friends = [];
			result.records.forEach(function(record) {
				friends.push(record.get("are_friends"));
			});
		const obj = {"friends": friends};
		resolve(obj);
        session.close();
		})
		.catch(function(error) {
			console.log("me "+ error);
		});
	});