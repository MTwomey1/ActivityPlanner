'use strict';

var express = require('express')
var app = express()
const router 	   = express.Router()
const port = process.env.PORT || 8080;
const bodyParser = require('body-parser')
const logger = require('morgan')

app.use(bodyParser.json());
app.use(logger('dev'));

app.use(express.static(__dirname + '/public'))

require('./routes')(router);
//app.use('/api/v1', router);
app.use(router);

app.listen(port);

console.log(`App Runs on ${port}`);

//module.exports = app;
