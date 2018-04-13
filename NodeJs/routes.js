'use strict';

const register = require('./functions/register');
const add_friend = require('./functions/add_friend');
const view_friends = require('./functions/view_friends');
const add_activities = require('./functions/add_activities');
const get_suggestions = require('./functions/get_suggestions');
const get_activities = require('./functions/get_activities');
const check_friends = require('./functions/check_friends');

module.exports = router => {

	router.get('/', (req, res) => res.end('Welcome to ActivityPlanner !!!'));

	// @POST("user")
	router.post('/user', (req, res) => {

		const user_username = req.body.username;
		const user_firstname = req.body.firstname;
		const user_lastname = req.body.lastname;
		console.log(user_username);

		if (!user_username || !user_firstname || !user_lastname || !user_username.trim() || !user_firstname.trim() || !user_lastname.trim()) {

			res.status(400).json({message: 'Invalid Request !'});

		} else {

			register.registerUser(user_username, user_firstname, user_lastname)

			.then(result => {

				//res.setHeader('Location', '/user/'+user_username);
				res.status(result.status).json({ message: result.message })
			})
			.catch(err => res.status(err.status).json({ message: err.message }));
			
			res.send('It Works')
		}
	});
	
	// @POST("friend")
	router.post('/friend', (req, res) => {

		const user_username1 = req.body.username1;
		const user_username2 = req.body.username2;
		//console.log(user_username1 + ' ' + user_username2);

		if (!user_username1 || !user_username2 || !user_username1.trim() || !user_username2.trim()) {

			res.status(400).json({message: 'Invalid Request !'});

		} else {

			add_friend.addFriend(user_username1, user_username2)

			.then(result => {

				//res.setHeader('Location', '/user/'+user_username);
				res.status(result.status).json({ message: result.message })
			})
			.catch(err => res.status(err.status).json({ message: err.message }));
			
			res.send('It Works')
		}
	});
	
	// @POST("myFriends")
	router.post('/myFriends', (req, res) => {

		const user_username = req.body.username;

		if (!user_username || !user_username.trim() ) {

			res.status(400).json({message: 'Invalid Request !'});

		} else {

			view_friends.viewFriends(user_username)

			.then(result => {
				
				console.log("2nd "+ JSON.stringify(result));

				//res.json('{"username": ' + JSON.stringify(result) + '}');
				res.json(result);
			})
			.catch(err => res.status(err.status).json({ message: err.message }));
			
		}
	});
	
	// @POST("addActivities")
	router.post('/addActivities', (req, res) => {

		const user_username = req.body.username;
		const activities = req.body.activity
		
		var output = new Array();
		output = activities.toString().split(",");
		//console.log(output);

		if (!user_username || !user_username.trim() ) {

			res.status(400).json({message: 'Invalid Request !'});

		} else {

			add_activities.addActivities(user_username, output)

			.then(result => {
				
				//console.log("2nd "+ JSON.stringify(result));

				//res.json('{"username": ' + JSON.stringify(result) + '}');
				res.send("Yoweed");
			})
			.catch(err => res.status(err.status).json({ message: err.message }));
			
		}
	});
	
	// @POST("getSuggestions")
	router.post('/getSuggestions', (req, res) => {

		const user_username = req.body.username;

		if (!user_username || !user_username.trim() ) {

			res.status(400).json({message: 'Invalid Request !'});

		} else {

			get_suggestions.getSuggestions(user_username)

			.then(result => {
				
				console.log("2nd "+ JSON.stringify(result));
				res.json(result);
			})
			.catch(err => res.status(err.status).json({ message: err.message }));
			
		}
	});
	
	// @POST("getActivities")
	router.post('/getActivities', (req, res) => {

		const user_username = req.body.username;

		if (!user_username || !user_username.trim() ) {

			res.status(400).json({message: 'Invalid Request !'});

		} else {

			get_activities.getActivities(user_username)

			.then(result => {
				
				//console.log("2nd "+ JSON.stringify(result));
				res.json(result);
			})
			.catch(err => res.status(err.status).json({ message: err.message }));
			
		}
	});
	
	// @POST("checkFriends")
	router.post('/checkFriends', (req, res) => {

		const user_username1 = req.body.username1;
		const user_username2 = req.body.username2;

		if (!user_username1 || !user_username2 || !user_username1.trim() || !user_username2.trim()) {

			res.status(400).json({message: 'Invalid Request !'});

		} else {

			check_friends.checkFriends(user_username1, user_username2)

			.then(result => {

				console.log(result);
				res.json(result);
			})
			.catch(err => res.status(err.status).json({ message: err.message }));
			
			
		}
	});


}