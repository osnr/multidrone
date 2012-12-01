MultiDrones = require('../../bin/MultiDrones')

Drones = [
#    MultiDrones.createClient(ip: '192.168.1.201')
    MultiDrones.createClient(ip: '192.168.1.200')
]

drone.takeoff() for drone in Drones
drone.config('general:navdata_demo','FALSE') for drone in Drones
drone.clockwise(1) for drone in Drones
drone.front(1) for drone in Drones
drone.after(10000,->
	@stop()
	@land()
) for drone in Drones