MultiDrones = require('../../src/MultiDrones')
MultiClients = require('../../src/Clients')

Drones = [
    MultiDrones.createClient(ip: '192.168.1.201')
    MultiDrones.createClient(ip: '192.168.1.200')
]

drone.takeoff() for drone in Drones
drone.config('general:navdata_demo','FALSE')
drone.on('navdata',(data)->
	if(data? and data['demo']?)
		v = data['demo'].velocity
		console.log 'x Velocity: ' + v['x']
		console.log 'y Velocity: ' + v['y']
		console.log 'z Velocity: ' + v['z']
) for drone in Drones

drone
.after(3000,->
	@stop()
	@land()
) for drone in Drones