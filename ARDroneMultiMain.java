/*********************************************************************
 Filename     : ARDroneMultiMain.java
 Author       : Innocent E.Okoloko.
 Date         : Wed Nov 1 8:37:32 EDT 2011
 Modified     : Mon Nov 05 11:05:32 EDT 2012
 Version      : 1.0

 Description:
	This class creates a number of instances of the ARDroneMulti class
	using the IP addresses assigned to the multiple drones by a managed
	network infrastructure.
	ARDroneMulti is an abstraction of an AR.Drone. Once multiple drones
	are created, they can be controlled simoultaneously.

	This class gives an example of controlling 3 AR.Drones.
	It is straightforward to create more drones and control them.
**********************************************************************/

import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.*;
import java.io.*;
import java.util.*;
import javax.swing.JComponent;

class ARDroneMultiMain
{

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //main
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public static void main(String args[]) throws Exception {
	float vel = (float)0.1;
	int takeoffTime=30;
	int forwardTime=10; //forward

	//int forwardTime=45;	//yaw
	int moveRightTime=20;

	int timeCounter=0;


	//ASSUMING YOUR DHCP SERVER ON YOUR ROUTER HAS ASSIGNED THESE IP ADDRESSES
	String ip1 = "192.168.1.1";
	//String ip1 = "192.168.0.101";
	String ip2 = "192.168.0.102";
	String ip3 = "192.168.0.103";


	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	//CREATE FIRST DRONE
	ARDroneMulti ardrone1 = new ARDroneMulti("ARDroneMulti", ip1);

	//CREATE SECOND DRONE
	ARDroneMulti ardrone2 = new ARDroneMulti("ARDroneMulti", ip2);

	//CREATE THIRD DRONE
	ARDroneMulti ardrone3 = new ARDroneMulti("ARDroneMulti", ip3);
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@


	Thread.sleep(ARDroneMulti.INTERVAL);
	NavData nd1 = new NavData(ardrone1, ardrone1.inet_addr);
	nd1.start();

	Thread.sleep(ARDroneMulti.INTERVAL);

	//SEE THE VIDEO DATA FOR ANY DRONE
	//Video v1 = new Video(ardrone1, ardrone1.inet_addr);
	//v1.start();

	//Thread.sleep(ARDroneMulti.INTERVAL);
	ardrone1.send_at_cmd("AT*REF=" + ardrone1.get_seq() + ",290717696");
	ardrone2.send_at_cmd("AT*REF=" + ardrone2.get_seq() + ",290717696");
	ardrone3.send_at_cmd("AT*REF=" + ardrone3.get_seq() + ",290717696");
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	//CONTROL DRONE BY READING CONTROL DATA FROM A FILE
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	//String fname="3SCAvoidanceFull1.txt";
	String fname="ctrlFile1.txt";
	int i=0;
	int numfile=0;
	int numControl=4;
	int numVehicles=3;
	int numStates=3;


	//double[][] refStates = new double [1000][numStates*numVehicles];
	float[][] ctrInputs = new float [1000][numVehicles*numControl];


	//IF YOU PREFER TO USE THE FILE CHOOSER TO LOAD THE CONTROL FILE YOURSELF
	/*
	JFileChooser chooser = new JFileChooser();
	chooser.setCurrentDirectory(new File("."));

	//accept all .gif file
	chooser.setFileFilter(new
	javax.swing.filechooser.FileFilter()
	{
			public boolean accept(File f)
			{
					return f.getName().toLowerCase().endsWith(".txt")||f.isDirectory();
			}
			public String getDescription()
			{
					return "Text files";
			}
	});

	//show file chooser dialog
	int r=chooser.showOpenDialog(ARDroneMulti.this);//ImageViewerFrame.this);

	//if image is accepted set it as icon of the label
	if(r==JFileChooser.APPROVE_OPTION)
	{
			fname=chooser.getSelectedFile().getPath();
			//label.setIcon(new ImageIcon(name));
	}*/

	try
	{
		BufferedReader in = new BufferedReader(
		new FileReader(fname));

		//****************************
		System.out.println(fname);

		String str;
		while ((str = in.readLine()) != null)
		{
		  StringTokenizer t = new StringTokenizer(str, " ");
		  int numtokens = t.countTokens();

		  for (int cols=0; cols<9; cols++)
		  {
			ctrInputs[i][cols]= Float.parseFloat(t.nextToken());
		  }
		  System.out.println(" ");

		i++;
		}
		in.close();
		numfile=i;

	}
	catch (IOException e)
	{

	}

	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	//CONTROL DRONES WITH FILE DATA
	//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@


		int j=0;

        while (true) {
	    if (timeCounter<=1)
	    {
	      System.out.println("Taking off...");
	      ardrone1.takeOff();
	      ardrone2.takeOff();
	      ardrone3.takeOff();
	      //System.out.println("timeCounter: " + timeCounter + " State: " + ardrone1.droneState + " Battery: " + ardrone1.dronePower + " Altitude: " + ardrone.droneZ + " Yaw: " + ardrone1.yaw + " Pitch: " + ardrone1.pitch + " Roll: " + ardrone1.roll);
	    }
	    if (timeCounter>=takeoffTime && timeCounter<=(takeoffTime+forwardTime))
	    {
	      //forward vel
	      if (timeCounter==takeoffTime)
			  System.out.println("Forward...");


			//I COMMENTED THIS OUT JUST FOR SAFETY REASONS, IF YOU HAVE ENOUGH SPACE FOR FORWARD
			//MOTION WHILE TESTING THIS CODE YOU CAN UNCOMMENT
			//SYNTAX=ardrone1.send_pcmd(1, right/left, forw/back, up/down, rotright/left);
			//ardrone1.send_pcmd(1, ctrInputs[j][0], ctrInputs[j][1], ctrInputs[j][2], ctrInputs[j][3]);
			//ardrone2.send_pcmd(1, ctrInputs[j][4], ctrInputs[j][5], ctrInputs[j][6], ctrInputs[j][7]);
			//ardrone3.send_pcmd(1, ctrInputs[j][8], ctrInputs[j][9], ctrInputs[j][10], ctrInputs[j][11]);

	    }
	    if (timeCounter>(takeoffTime+forwardTime) && timeCounter<=(takeoffTime+forwardTime)+5)
	    {
	      //Hover
	      ardrone1.Hover();
	      ardrone2.Hover();
	      ardrone3.Hover();
	    }
	    if (timeCounter>(takeoffTime+forwardTime)+5)
	    {
	      if (timeCounter==(takeoffTime+forwardTime)+6)
			System.out.println("Landing...");
	      	ardrone1.Landing();
	      	ardrone2.Landing();
	      	ardrone3.Landing();
	      	//System.out.println("timeCounter: " + timeCounter + " State: " + ardrone1.droneState + " Battery: " + ardrone1.dronePower + " Altitude: " + ardrone1.droneZ + " Yaw: " + ardrone1.yaw + " Pitch: " + ardrone1.pitch + " Roll: " + ardrone1.roll);
	    }


		ardrone1.seq_last = ardrone1.seq;
		ardrone2.seq_last = ardrone2.seq;
		ardrone3.seq_last = ardrone3.seq;

        Thread.sleep(200);

        if (ardrone1.seq == ardrone1.seq_last) ardrone1.send_at_cmd(ardrone1.at_cmd_last);
	    if (ardrone2.seq == ardrone2.seq_last) ardrone2.send_at_cmd(ardrone2.at_cmd_last);
	    if (ardrone3.seq == ardrone3.seq_last) ardrone3.send_at_cmd(ardrone3.at_cmd_last);

	    timeCounter++;
	    j++;
	    //System.out.println("timeCounter: " + timeCounter + " State: " + ardrone1.droneState + " Battery: " + ardrone1.dronePower + " Altitude: " + ardrone1.droneZ + " Yaw: " + ardrone1.yaw + " Pitch: " + ardrone1.pitch + " Roll: " + ardrone1.roll);
	}
    }
   /********************************************************************************************
    * end main
    ********************************************************************************************/
}
