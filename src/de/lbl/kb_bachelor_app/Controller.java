package de.lbl.kb_bachelor_app;

import de.lbl.kb_bachelor_app.network.*;
import java.util.*;
import android.util.*;

public class Controller
{
	public final static String TAG = "Controller: ";
	
	Client client;
	ClientAct cAct;

	Queue<ControlAction> schedule;
	Thread scheThread;

	public Controller(ClientAct act)
	{
		cAct = act;
		client = new Client();
		schedule = new LinkedList<ControlAction>();
		scheThread = newScheduleThread();
		scheThread.start();
	}

	public Thread newScheduleThread()
	{
		return new Thread(new Runnable(){
				@Override
				public void run()
				{
					while (true)
					{
						while (!schedule.isEmpty())
						{
							Log.d(TAG, "Try next scheduled action");
							ControlAction ca = schedule.poll();
							switch (ca.action)
							{
								case CONNECT:
									String serverIp = ca.serverIp;
									int serverPort = ca.serverPort;
									connect(serverIp, serverPort);
									break;
								case DISCONNECT:
									disconnect();
									break;
								case SEND:
									String m = ca.message;
									sendChatMessage(m);
									break;
							}
							addToPool(ca);
						}
					}
				}
			});
	}

	public void scheduleAction(ControlAction ac)
	{
		schedule.add(ac);
	}

	private void connect(String serverIp, int serverPort)
	{
		Log.d(TAG,"try connecting");
		if (client.state.equals(Client.State.DISCONNECTED))
		{ 
			Log.d(TAG,"is not connected");
			if (!serverIp.equals(""))
			{
				Log.d(TAG,"ip not empty"); // bessere erkennung ob gültige ip
				client.serverIpAddress = serverIp;
				client.serverPort = serverPort;
				client.connect();
			}
			else
			{
				String m = "Empty IP, please enter IP";
				Log.d(TAG,m);
				writeQuickInfo(m);
			}
		}
		else
		{
			String m = "allready connected";	
			Log.d(TAG,m);
			writeQuickInfo(m);
		}
	}

	private void disconnect()
	{
		Log.d(TAG,"diconnect");
		client.disconnect();
		writeQuickInfo("disconnected");
	}

	private void sendChatMessage(String m)
	{
		NetworkCommand nc = getNewNetworkCommand();
		nc.setCommand(NetworkCommand.Commands.SENDMESSAGE);
		nc.setMessage(m);
		client.scheduleCommand(nc);
	}

	public void writeQuickInfo(String m)
	{
		final String me = m;
		cAct.handler.post(new Runnable(){
			@Override
			public void run(){
				cAct.quickInfoTV.setText(me);
			}
		});
	}

	public ControlAction getNewAction()
	{
		// TODO: Hier ne Fabrik + pool bauen damit wir die objecte immer wieder verwenden 
		return new ControlAction();
	}

	public void addToPool(ControlAction ca)
	{
		//TODO H
	}

	private NetworkCommand getNewNetworkCommand()
	{
		// TODO: auch hier ne fabrik
		return new NetworkCommand();
	}

	public void tryAction(ControlAction ac)
	{
		// TODO: Implement this method
	}
}
