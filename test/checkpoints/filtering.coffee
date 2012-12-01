MultiDrones = require('../../bin/MultiDrones')
Filter = require('../../bin/filter')

drones = [
    # MultiDrones.createClient(ip: '192.168.1.201')
    MultiDrones.createClient(ip: '192.168.1.201')
]

filter = new Filter.Filter()

drones[0].on 'navdata',filter.update