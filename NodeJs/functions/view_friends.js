'use strict';

var neo4j = require('neo4j-driver').v1;
var driver = neo4j.driver("bolt://hobby-hncajieppajlgbkebcacggal.dbs.graphenedb.com:24786", neo4j.auth.basic("myapp", "b.K8YbBk5dbqUi.1NjWuVJWFXZvFFN6"));
var session = driver.session();

exports.viewFriends = (user_username) =>
	
	new Promise((resolve, reject) => {
		console.log(user_username)
		session
		.run('MATCH (u:User {username: $username}) -[:FRIEND_OF]-(n) RETURN n.username',
			{'username': user_username})
		 .then(function(result) {
			var friends = [];
			result.records.forEach(function(record) {
				//console.log(record.get('n.username'));
				friends.push(record.get('n.username'));
        });
		const obj = {"friends": friends};
		resolve(obj)
		//resolve(record)
        session.close();
		})
		.catch(error => reject({ status: 500, message: 'Internal Server Error !' }))
	});