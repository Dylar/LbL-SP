package de.lbl.kb_bachelor_app.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;
import de.lbl.kb_bachelor_app.control.ControlAction;
import de.lbl.kb_bachelor_app.control.ControlHandler;
import de.lbl.kb_bachelor_app.control.ControlListener;
import de.lbl.kb_bachelor_app.control.Controller;

public class TCPClient implements ControlListener
{
	public final static String	TAG	= "Client: ";

	public enum State
	{
		CONNECTED, DISCONNECTED
	}

	public ControlHandler			ctrl;

	public int							ID;
	public String						serverIpAddress;
	public int							serverPort;

	public State						state;
	public Queue<NetworkCommand>	commands;

	private BufferedReader			in;
	private PrintWriter				out;


	public TCPClient()
	{
		serverIpAddress = "";
		serverPort = 8080;
		state = State.DISCONNECTED;
		commands = new LinkedList<NetworkCommand>();
	}


	public void disconnect()
	{
		state = State.DISCONNECTED;
		ControlAction ca = ctrl.getNewAction();
		ca.setAction(ControlHandler.DISCONNECT); // TODO das ist wegen GUI
																// melden oder wir machen das
																// im controller sowieso ...
		tryAction(ca);
	}


	public void connect()
	{
		Thread cThread = new Thread(getNewClientRun());
		cThread.start();
	}


	public void scheduleCommand(NetworkCommand nc)
	{
		// TODO schnittstelle f�r drau�en
		Log.d(TAG, "command scheduled");
		commands.add(nc);
	}


	private Runnable getNewClientRun()
	{
		return new Runnable(){

			public void run()
			{
				try
				{
					InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
					Log.d(TAG, "C: Connecting...");
					Socket socket = new Socket(serverAddr, serverPort);

					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					state = State.CONNECTED;

					while (state.equals(State.CONNECTED))
					{
						if (!commands.isEmpty())
						{
							Log.d(TAG, "C: Sending command.");

							writeOutput(getNextCommand());
						}
						if(in.ready())
							readInput();
						Thread.sleep(420);
					}
					socket.close();
					Log.d(TAG, "C: Closed.");
				}
				catch (Exception e)
				{
					Log.e(TAG, "C: Error", e);
					state = State.DISCONNECTED;
				}
			}
		};
	}


	private NetworkCommand getNextCommand()
	{
		return commands.poll();
	}


	private synchronized void readInput() throws IOException
	{
		// TODO
		Log.d(TAG, " read income");

		ControlAction ca = ctrl.getNewAction();
		ca.setAction(Integer.valueOf(in.readLine()));

		switch (ca.action)
		{
			case ControlHandler.SEND_ID:// send ID
				ca.setID(Integer.valueOf(in.readLine()));
				// ca.setID(ID);
				Log.d(TAG, " GET ID");
				break;
			case ControlHandler.SEND_MESSAGE: // send Message
				ca.action = ControlHandler.GET_MESSAGE;
				ca.message = in.readLine();
				Log.d(TAG, " getting MESSAGE");
				break;
			case ControlHandler.GET_MESSAGE: // get message

				Log.d(TAG, " GET MESSAGE");
				break;
			default:
				break;
		}

		tryAction(ca);
	}


	private synchronized void writeOutput(NetworkCommand nc)
	{
		// TODO output schreiben
		Log.d(TAG, "write output");
		out.println(nc.command);
		switch (nc.getCommand())
		{
			case ControlHandler.SEND_ID:
				out.println(nc.ID);
				break;
			case ControlHandler.SEND_MESSAGE:
				out.println(nc.message);
				break;
			
			default:
				//out.println(TAG + "Hey Server! was geht");
				break;
		}
		out.flush();
	}


	@Override
	public void tryAction(ControlAction ca)
	{
		ctrl.scheduleAction(ca);
	}


	@Override
	public void addController(ControlHandler ch)
	{
		this.ctrl = ch;
	}
}
