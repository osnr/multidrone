MultiDrones = require('../../bin/MultiDrones')

drones = [
    MultiDrones.createClient(ip: '192.168.1.201')
    MultiDrones.createClient(ip: '192.168.1.200')
]


logData= (name)->
	console.log "Data from " + name

drones[0].on 'navdata', (dat)->
	logData("first")

drones[1].on 'navdata', (dat)->
	logData("second")