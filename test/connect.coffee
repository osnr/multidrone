arDrone = require('ar-drone')
clients = [
    arDrone.createClient(ip: '192.168.1.200'),
    arDrone.createClient(ip: '192.168.1.201')
]

for i, client of clients
    console.log i
    client.on 'navdata', (navdata) => console.log i, navdata
