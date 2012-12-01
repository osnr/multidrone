MultiDrones = require('../../src/MultiDrones')

clients = [
    MultiDrones.createClient(ip: '192.168.1.201')
    MultiDrones.createClient(ip: '192.168.1.200')
]

logData= (name)->
	console.log "Data from " + name

clients[0].on 'navdata', (dat)->
	logData("first")

clients[1].on 'navdata', (dat)->
	logData("second")

# for client, i in clients
#     console.log i
#     client.on 'navdata', (navdata) => 
#     	console.log "Data from ", if i is 0 then "first" else "second"
