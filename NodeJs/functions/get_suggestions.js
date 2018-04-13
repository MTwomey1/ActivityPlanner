'use strict';

var neo4j = require('neo4j-driver').v1;
var driver = neo4j.driver("bolt://hobby-hncajieppajlgbkebcacggal.dbs.graphenedb.com:24786", neo4j.auth.basic("myapp", "b.K8YbBk5dbqUi.1NjWuVJWFXZvFFN6"));
var session = driver.session();

exports.getSuggestions = (user_username) =>
	
	new Promise((resolve, reject) => {
		console.log(user_username)
		session
		.run('MATCH (u:User {username: $username}) -[f1:FRIEND_OF]-()-[f2:FRIEND_OF]-(m:User) WHERE NOT (u:User)-[:FRIEND_OF]-(m:User) RETURN DISTINCT m.username',
			{'username': user_username})
		 .then(function(result) {
			var friends = [];
			result.records.forEach(function(record) {
				console.log(record.get('m.username'));
				friends.push(record.get('m.username'));
        });
		const obj = {"friends": friends};
		resolve(obj)
		//resolve(record)
        session.close();
		})
		.catch(error => reject({ status: 500, message: 'Internal Server Error !' }))
	});