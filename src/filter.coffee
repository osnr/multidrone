Sylvester = require('Sylvester')

class exports.Filter
	'''Kalman Filter'''
	data =
		position :
			x : 0
			y : 0
			z : 0
		velocity :
			x : 0
			y : 0
			z : 0

	x = $M([[0], [0]]) # initial state (location and velocity)
	P = $M([[1000, 0], [0, 1000]]) # initial uncertainty
	u = $M([[0], [0]]) # external motion
	F = $M([[1, 1], [0, 1]]) # next State function
	H = $M([[1, 0]]) # measurement function
	R = $M([[1]]) # measurement uncertainty
	I = $M([[1, 0], [0, 1]]) # identity matrix

	'''Takes in navdata from event listener and applies'''
	update: (navdata)=>
		console.log navdata
		measurements = {
			position : [x, y, z ],
			velocity : [vx,vy,vz]
		}
		apply(measurements['position'],measurements['velocity'])
		


	'''Applies the kalman filter to passed nav data'''
	apply: (position, velocity) =>
		measurements = [position,velocity]
		for measurement,i in measurements

			#measurement update
			Z = $M [[measurement]]
			y = Z.subtract (H.x x)
			S = H.x(P).x(H.transpose()).plus(R)
			K = P.x(H.transpose()).x S.inv
			x = x.plus K.x y

			P = I.minus(K.x(H)).x P

			#prediction
			x = F.x(x).plus(u)
			P = F.times(P).times(F.transpose())

			console.log "Matrix x #" + (i+1) + ":"
			console.log x.inspect()
			console.log "Matrix P #" + (i+1) + ":"
			console.log P.inspect()
			
