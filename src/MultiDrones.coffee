exports;

#Todo:
# - Modify library's index for multidrones
# - Ensure PngStream and family work with multidrones too
Client = require('../lib/node-ar-drone/lib/Client')
UdpControl       = require('../lib/node-ar-drone/lib/control/UdpControl');
PngStream        = require('../lib/node-ar-drone/lib/video/PngStream');
UdpNavdataStream = require('../lib/node-ar-drone/lib/navdata/UdpNavdataStream');

exports.createClient = (options)->
	client = new Client(options)
	client.resume()
	return client

exports.createUdpControl = (options) ->
	return new UdpControl(options)

exports.createPngStream = (options) ->
	stream = new PngStream(options)
	stream.resume()
	return stream

exports.createUdpNavdataStream = (options) ->
	stream = new UdpNavdataStream(options)
	stream.resume()
	return stream