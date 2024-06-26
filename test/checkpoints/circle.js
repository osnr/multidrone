// Generated by CoffeeScript 1.4.0
(function() {
  var Drones, MultiDrones, drone, _i, _j, _k, _l, _len, _len1, _len2, _len3, _len4, _m;

  MultiDrones = require('../../bin/MultiDrones');

  Drones = [
    MultiDrones.createClient({
      ip: '192.168.1.200'
    })
  ];

  for (_i = 0, _len = Drones.length; _i < _len; _i++) {
    drone = Drones[_i];
    drone.takeoff();
  }

  for (_j = 0, _len1 = Drones.length; _j < _len1; _j++) {
    drone = Drones[_j];
    drone.config('general:navdata_demo', 'FALSE');
  }

  for (_k = 0, _len2 = Drones.length; _k < _len2; _k++) {
    drone = Drones[_k];
    drone.clockwise(1);
  }

  for (_l = 0, _len3 = Drones.length; _l < _len3; _l++) {
    drone = Drones[_l];
    drone.front(1);
  }

  for (_m = 0, _len4 = Drones.length; _m < _len4; _m++) {
    drone = Drones[_m];
    drone.after(10000, function() {
      this.stop();
      return this.land();
    });
  }

}).call(this);
