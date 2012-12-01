/*********************************************************************
 Filename     : ARDroneMulti.java
 Author       : Innocent E.Okoloko

 				Adapted from MAPGPS's program for keyboard control
 				By MAPGPS on
				https://projects.ardrone.org/projects/ardrone-api/boards
				http://bbs.5imx.com/bbs/forumdisplay.php?fid=453

 Date         : Wed Nov 1 8:37:32 EDT 2011
 Modified     : Mon Nov 05 11:05:32 EDT 2012
 Version      : 1.0

 Description:
	This class is an abstraction of an AR.Drone. Any number of instances
	can be created.
**********************************************************************/

//import auxfiles.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.*;


public class ARDroneMulti implements Runnable {
    static final int NAVDATA_PORT = 5554;
    static final int VIDEO_PORT   = 5555;
    static final int AT_PORT 	  = 5556;



    //The first index is the header, second is drone state, third is sequence number,
    //4th is vision flag, 5th and 6th are option 1 id and size, 7th is battery percentage,
    //8th is pitch, 9th is roll, 10th is yaw, 11th is altitude.
    //NavData offset
    static final int NAVDATA_STATE    =  4;
    static final int NAVDATA_BATTERY  = 24;
    static final int NAVDATA_PITCH    = 28;
    static final int NAVDATA_ROLL     = 32;
    static final int NAVDATA_YAW      = 36;
    static final int NAVDATA_ALTITUDE = 40;

    static float droneZ;
    static int dronePower;
    static int droneState;
    static float yaw;
    static float pitch;
    static float roll;

    InetAddress inet_addr;
    DatagramSocket socket_at;
    DatagramSocket socket_nav;

    int seq = 1; //Send AT command with sequence number 1 will reset the counter
    int seq_last = 1;
    String at_cmd_last = "";
    float speed = (float)0.1;
    boolean shift = false;
    FloatBuffer fb;
    IntBuffer ib;
    boolean space_bar = false; //true = Takeoff, false = Landing
    final static int INTERVAL = 100;

    //IF YOU WANT TO VIEW IMAGE DATA
    //public ReadRawDisplayImagePanel imPanel=new ReadRawDisplayImagePanel();

    public ARDroneMulti(String name, String ip) throws Exception
    {

		StringTokenizer st = new StringTokenizer(ip, ".");

		byte[] ip_bytes = new byte[4];
		if (st.countTokens() == 4){
			for (int i = 0; i < 4; i++){
			ip_bytes[i] = (byte)Integer.parseInt(st.nextToken());
			}
		}
		else {
			System.out.println("Incorrect IP address format: " + ip);
			System.exit(-1);
		}

		//System.out.println("IP: " + ip);
		inet_addr = InetAddress.getByAddress(ip_bytes);


		this.inet_addr = inet_addr;
		//System.out.println(this.inet_addr);

		ByteBuffer bb = ByteBuffer.allocate(4);
		fb = bb.asFloatBuffer();
		ib = bb.asIntBuffer();

		socket_at = new DatagramSocket();


		send_at_cmd("AT*PMODE=" + get_seq() + ",2");
		Thread.sleep(INTERVAL);
		send_at_cmd("AT*REF=" + get_seq() + ",290717696");
		Thread.sleep(INTERVAL);
		send_at_cmd("AT*COMWDG=" + get_seq());
		Thread.sleep(INTERVAL);
		send_at_cmd("AT*CONFIG=" + get_seq() + ",\"control:altitude_max\",\"500\""); //altitude max 1m
		Thread.sleep(INTERVAL);
		send_at_cmd("AT*CONFIG=" + get_seq() + ",\"control:control_level\",\"0\""); //0:BEGINNER, 1:ACE, 2:MAX
		Thread.sleep(INTERVAL);
		send_at_cmd("AT*CONFIG=" + get_seq() + ",\"general:navdata_demo\",\"FALSE\"");
		Thread.sleep(INTERVAL);
		send_at_cmd("AT*CONFIG=" + get_seq() + ",\"general:video_enable\",\"TRUE\"");
		Thread.sleep(INTERVAL);

		send_at_cmd("AT*CONFIG=" + get_seq() + ",\"pic:ultrasound_freq\",\"8\"");
		Thread.sleep(INTERVAL);

		Thread wdg = new Thread(this);
		wdg.start();

    }


    public int intOfFloat(float f) {
        fb.put(0, f);

        return ib.get(0);
    }

    public static String byte2hex(byte[] data, int offset, int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            String tmp = Integer.toHexString(((int) data[offset + i]) & 0xFF);
            for(int t = tmp.length();t<2;t++)
            {
                sb.append("0");
            }
            sb.append(tmp);
            sb.append(" ");
        }
        return sb.toString();
    }

    public static int get_int(byte[] data, int offset) {
		int tmp = 0, n = 0;

		for (int i=3; i>=0; i--) {
			n <<= 8;
			tmp = data[offset + i] & 0xFF;
			n |= tmp;
		}

		return n;
    }

    public synchronized int get_seq() {
    	return seq++;
    }

    public void send_pcmd(int enable, float pitch, float roll, float gaz, float yaw) throws Exception {
	send_at_cmd("AT*PCMD=" + get_seq() + "," + enable + "," + intOfFloat(pitch) + "," + intOfFloat(roll)
    	    				+ "," + intOfFloat(gaz) + "," + intOfFloat(yaw));
    }


    public synchronized void send_at_cmd(String at_cmd) throws Exception {
		at_cmd_last = at_cmd;
		byte[] buf_snd = (at_cmd + "\r").getBytes();
		DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, ARDroneMulti.AT_PORT);
		socket_at.send(packet_snd);

    }

    public void setVel()  throws Exception
    {
      send_at_cmd("AT*REF=" + get_seq() + ",290718208");
    }

    public void takeOff()  throws Exception
    {
      send_at_cmd("AT*REF=" + get_seq() + ",290718208");
    }

    public void Landing()  throws Exception
    {
      send_at_cmd("AT*REF=" + get_seq() + ",290717696");
    }

    public void Hover()  throws Exception
    {
      send_pcmd(1, 0, 0, 0, 0);
    }

	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     public void run()
     {
		while(true){
			//System.out.println("Setting watchdog command...");
			String at_cmd = "AT*COMWDG=" + (get_seq());
			try{
			send_at_cmd(at_cmd);
			}catch(Exception ex){

			}
			try{
				Thread.sleep(30);
			}catch(InterruptedException iex){
			}
		}
      }
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
}


/********************************************************************************************
* CLASS NavData, GET NAVIGATION DATA
********************************************************************************************/
class NavData extends Thread {
    DatagramSocket socket_nav;
    InetAddress inet_addr;
    ARDroneMulti ardrone;

    public NavData(ARDroneMulti ardrone, InetAddress inet_addr) throws Exception {
    	this.ardrone = ardrone;
	this.inet_addr = inet_addr;

	socket_nav = new DatagramSocket(ARDroneMulti.NAVDATA_PORT);
	socket_nav.setSoTimeout(300);
    }

    public void run() {
    	int cnt = 0;

	try {
	    byte[] buf_snd = {0x01, 0x00, 0x00, 0x00};
	    DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, ARDroneMulti.NAVDATA_PORT);
	    socket_nav.send(packet_snd);
    	    System.out.println("Sent trigger flag to UDP port " + ARDroneMulti.NAVDATA_PORT);

	    ardrone.send_at_cmd("AT*CONFIG=" + ardrone.get_seq() + ",\"general:navdata_demo\",\"TRUE\"");

 	    byte[] buf_rcv = new byte[10240];
	    DatagramPacket packet_rcv = new DatagramPacket(buf_rcv, buf_rcv.length);

	    while(true) {
		try {
		    socket_nav.receive(packet_rcv);

		    cnt++;
		    if (cnt >= 5) {
		    	cnt = 0;


			ARDroneMulti.droneState = ((int)ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_STATE)/1000);
			ARDroneMulti.droneZ = ((float)ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_ALTITUDE)/500);
			ARDroneMulti.dronePower=ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_BATTERY);
			ARDroneMulti.yaw=((float)ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_YAW)/1000000);
			ARDroneMulti.pitch=((float)ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_PITCH)/1000000);
			ARDroneMulti.roll=((float)ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_ROLL)/1000000);

			//System.out.println("State: " + ARDroneMulti.droneState + " Battery: " + ARDroneMulti.dronePower + " Altitude: " + ARDroneMulti.droneZ + " Yaw: " + ARDroneMulti.yaw + " Pitch: " + ARDroneMulti.pitch + " Roll: " + ARDroneMulti.roll);
			if (ARDroneMulti.droneState < 0)
			{
			    //System.out.println("Emergency State");
			    //ardrone.send_at_cmd("AT*REF=" + ardrone.get_seq() + ",290717952");
			    //Thread.sleep(50);
			    //ardrone.send_at_cmd("AT*REF=" + ardrone.get_seq() + ",290718208");
			    //Thread.sleep(50);
			}

		    	//System.out.println("Battery: " + ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_BATTERY)
		    	///		+ "%, Altitude: " + ((float)ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_ALTITUDE)/1000) + "m" + ", Yaw: " + ((float)ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_YAW)/1000000) + "rad" + ", Pitch: " + ((float)ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_PITCH)/1000000) + "rad" + ", Roll: " + ((float)ARDroneMulti.get_int(buf_rcv, ARDroneMulti.NAVDATA_ROLL)/1000000) + "rad");

		    }
		} catch(SocketTimeoutException ex3) {
	    	    System.out.println("socket_nav.receive(): Timeout");
		} catch(Exception ex1) {
		    ex1.printStackTrace();
		}
	    }
	} catch(Exception ex2) {
	    ex2.printStackTrace();
	}
    }
}
/********************************************************************************************
* END CLASS NavData, GET NAVIGATION DATA
********************************************************************************************/

/********************************************************************************************
* CLASS Video, GET  VISION DATA
********************************************************************************************/
/*
class Video extends Thread {
    DatagramSocket socket_video;
    InetAddress inet_addr;
    ARDroneMulti ardrone;

    public Video(ARDroneMulti ardrone, InetAddress inet_addr) throws Exception {
    	this.ardrone = ardrone;
	this.inet_addr = inet_addr;

	socket_video = new DatagramSocket(ARDroneMulti.VIDEO_PORT);
	socket_video.setSoTimeout(300);
    }

    public void run() {
	try {
	    byte[] buf_snd = {0x01, 0x00, 0x00, 0x00};
	    DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, ARDroneMulti.VIDEO_PORT);
	    socket_video.send(packet_snd);
    	    System.out.println("Sent trigger flag to UDP port " + ARDroneMulti.VIDEO_PORT);

	    ardrone.send_at_cmd("AT*CONFIG=" + ardrone.get_seq() + ",\"general:video_enable\",\"TRUE\"");

 	    byte[] buf_rcv = new byte[64000];
	    DatagramPacket packet_rcv = new DatagramPacket(buf_rcv, buf_rcv.length);

	    while(true) {
		try {
		    socket_video.receive(packet_rcv);

			//ardrone.imPanel.setVisible(false);
			ardrone.imPanel.getImage(buf_rcv);
			//ardrone.imPanel.setVisible(true);
			ardrone.repaint();

		    //System.out.println("Video Received: " + packet_rcv.getLength() + " bytes");
		    //System.out.println(ARDroneMulti.byte2hex(buf_rcv, 0, packet_rcv.getLength()));
		} catch(SocketTimeoutException ex3) {
	    	    System.out.println("socket_video.receive(): Timeout");
	    	    socket_video.send(packet_snd);
		} catch(Exception ex1) {
		    ex1.printStackTrace();
		}
	    }
	} catch(Exception ex2) {
	    ex2.printStackTrace();
	}
    }
}*/
/********************************************************************************************
* END CLASS Video, GET  VISION DATA
********************************************************************************************/
