// Generated by CoffeeScript 1.4.0
(function() {
  exports;

  var Client, PngStream, UdpControl, UdpNavdataStream;

  Client = require('ar-drone/lib/Client');

  UdpControl = require('ar-drone/lib/control/UdpControl');

  PngStream = require('ar-drone/lib/video/PngStream');

  UdpNavdataStream = require('ar-drone/lib/navdata/UdpNavdataStream');

  exports.createClient = function(options) {
    var client;
    client = new Client(options);
    client.resume();
    return client;
  };

  exports.createUdpControl = function(options) {
    return new UdpControl(options);
  };

  exports.createPngStream = function(options) {
    var stream;
    stream = new PngStream(options);
    stream.resume();
    return stream;
  };

  exports.createUdpNavdataStream = function(options) {
    var stream;
    stream = new UdpNavdataStream(options);
    stream.resume();
    return stream;
  };

}).call(this);
