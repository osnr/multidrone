MultiDrones = require('../../src/MultiDrones')
MultiDrones = require('../../src/MultiDrones')

Drones = [
#    MultiDrones.createClient(ip: '192.168.1.201')
    MultiDrones.createClient(ip: '192.168.1.200')
]

drone.takeoff() for drone in Drones
drone.config('general:navdata_demo','FALSE') for drone in Drones

setInterval ->
    drone.clockwise(1)
    drone.front(1)
, 700 for drone in Drones

