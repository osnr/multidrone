import roslib
import rospy
import numpy
#from Sensor_msg import Imu
#from ardrone/navdata import ardrone_autonomy::Navdata
from ardrone_autonomy.msg import Navdata
from geometry_msgs.msg import Twist


#intitalize parameters
x = 0
y = 0
y = 0
vx = Navdata.vx = 0
vy = Navdata.vy = 0
vz = Navdata.vz = 0

def callback(msg):
    print msg
measurements = [[x, y, z,], [vx, vy, vz]] # measurements from navdata
k_filter(measurements[0], measurements[1])
    #rospy.loginfo("Received a /cmd_vel message!")
    #rospy.loginfo("Linear Components: [%f, %f, %f]"%(msg.linear.x, msg.linear.y, msg.linear.z))
    
#def callback(data):
    #rospy.loginfo(rospy.get_name() + data.data)

# get IMU data from ardrone
#def listener():
 # rospy.init_node('listener', anonymous=True)
  #rospy.Subscriber("chatter", ardrone_autonomy::Navdata, callback)
   #rospy.spin()

def listener():
    rospy.init_node('Navdata_listener')
    rospy.Subscriber("Navdata", Navdata, callback)
    rospy.spin()

x = matrix([[0.], [0.]]) # initial state (location and velocity)
P = matrix([[1000., 0.], [0., 1000]]) # initial uncertainty
u = matrix([[0.], [0.]]) # external motion
F = matrix([[1., 1], [0, 1.]]) # next State function
H = matrix([[1., 0]]) # measurement function
R = matrix([[1.]]) # measurement uncertainty
I = matrix([[1., 0.], [0., 1.]]) # identity matrix

#filter IMU data from ardrone
def k_filter (x, P):

for n in range(len(measurements)):
	
	#measurement update
	Z = matrix([[measurements[n]]])
	y = Z - (H * x)
	S = H * P * H.transpose() + R
	K = P * H.transpose() * S.inverse()
	x = x + (K * y)

	P = (I -(K * H)) * P

	#prediction
	x = (F * x) + u
	P = F * P * F.transpose()
	
	#new_vx = measurements[[],[vx,vy,vz]
	twist.linear.x = 0 #filtered vx
	twist.linear.y =  0 #filtered vy
	twist.linear.z =  0 #filtered vz

	pub.publish(twist)

# publish filtered IMU data back to ardrone
def talker():
	pub = rospy.Publisher('cmd_vel', Twist)
    #pub = rospy.Publisher('chatter', Imu)
    #rospy.init_node('talker')
    #while not rospy.is_shutdown():
     #   str = "hello world %s" % rospy.get_time()
      #  rospy.loginfo(str)
       # pub.publish(String(str))
        #rospy.sleep(1.0)


if __name__ == '__main__':
	listener()
	#filter()
    #try:
     #   talker()
    #except rospy.ROSInterruptException:
     #   pass
