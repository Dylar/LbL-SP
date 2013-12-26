package de.lbl.kb_bachelor_app.network;


import android.util.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client
{
	public final static String TAG = "Client: ";
	
	public enum State
	{
		CONNECTED,DISCONNECTED
	}

    public String serverIpAddress = "";
	public int serverPort = 8080;

    public State state = State.DISCONNECTED;
	public Queue<NetworkCommand> commands = new LinkedList<NetworkCommand>();

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
	//TODO
		Log.d(TAG,"command scheduled");
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
                state = State.CONNECTED;
//
//				handler.post(new Runnable() {
//						@Override
//						public void run() {
//							returnText.setText("connected");
//						}
//					});

                while (state.equals(State.CONNECTED))
				{
                    try
					{
                        Log.d(TAG, "C: Sending command.");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

						if(!commands.isEmpty())
						{
							writeOutput(out);
							out.flush();
						}
						else
							Log.d(TAG,"no commands");
						
						readInput(in);
                    }
					catch (Exception e)
					{
                        Log.e(TAG, "S: Error", e);
                    }
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
			Log.d(TAG,"read income");
			// TODO: Implement this method
			//	while((line = in.readLine()) != null){
		//	line = in.readLine();
//						handler.post(new Runnable() {
//								@Override
//								public void run() {
//									returnText.setText(line);
//								}
//							});

			//		}

		}

		private void writeOutput(PrintWriter out)
		{
			Log.d(TAG,"write output");
			out.println("Hey Server! was geht sascha");
		}
    }
}
