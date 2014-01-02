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
import de.lbl.kb_bachelor_app.control.Controller;

public class Client
{
	public final static String	TAG	= "Client: ";

	public enum State
	{
		CONNECTED, DISCONNECTED
	}

	public Controller					ctrl;

	public String						serverIpAddress	= "";
	public int							serverPort			= 8080;

	public State						state					= State.DISCONNECTED;
	public Queue<NetworkCommand>	commands				= new LinkedList<NetworkCommand>();


	public Client(Controller controller)
	{
		ctrl = controller;
	}


	public void disconnect()
	{
		state = State.DISCONNECTED;
	}


	public void connect()
	{
		Thread cThread = new Thread(new ClientThread());
		cThread.start();
	}


	public void scheduleCommand(NetworkCommand nc)
	{
		// TODO
		Log.d(TAG, "command scheduled");
		commands.add(nc);
	}

	public class ClientThread implements Runnable
	{
		public void run()
		{
			try
			{
				InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
				Log.d(TAG, "C: Connecting...");
				Socket socket = new Socket(serverAddr, serverPort);

				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				state = State.CONNECTED;
				//
				// handler.post(new Runnable() {
				// @Override
				// public void run() {
				// returnText.setText("connected");
				// }
				// });

				while (state.equals(State.CONNECTED))
				{

					if (!commands.isEmpty())
					{
						Log.d(TAG, "C: Sending command.");

						writeOutput(out);
						out.flush();
					}

					readInput(in);

				}
				socket.close();
				Log.d(TAG, "C: Closed.");
			}
			catch (Exception e)
			{
				Log.e(TAG, "C: Error", e);
				state = state.DISCONNECTED;
			}
		}


		private void readInput(BufferedReader in) throws IOException
		{
		//	Log.d(TAG,"read income");
			// TODO: Implement this method
			// while((line = in.readLine()) != null){
			// line = in.readLine();
			// handler.post(new Runnable() {
			// @Override
			// public void run() {
			// returnText.setText(line);
			// }
			// });

			// }

		}


		private void writeOutput(PrintWriter out)
		{
			Log.d(TAG, "write output");
			out.println("Hey Server! was geht sascha");
		}
	}
}
