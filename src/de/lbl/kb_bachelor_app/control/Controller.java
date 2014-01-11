package de.lbl.kb_bachelor_app.control;

import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;
import de.lbl.kb_bachelor_app.network.TCPClient;
import de.lbl.kb_bachelor_app.network.NetworkCommand;
import de.lbl.kb_bachelor_app.network.gui.ClientAct;

public class Controller implements ControlHandler
{
	public final static String TAG = "Controller: ";
	
	TCPClient client;
	ClientAct guiAct;

	Queue<ControlAction> schedule;
	Thread actionThread;

	public Controller(ClientAct act)
	{
		guiAct = act;
		guiAct.addController(this);
		
		client = new TCPClient();
		client.addController(this);
		
		schedule = new LinkedList<ControlAction>();
		actionThread = newScheduleThread();
		actionThread.start();
	}

	public Thread newScheduleThread()
	{
		return new Thread(new Runnable(){
				@Override
				public void run()
				{
					waiting(actionThread);
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
								case SEND_MESSAGE:
									String m = ca.message;
									sendChatMessage(m);
									break;
								case SEND_ID:
									int id = ca.ID;
									setID(id);
									writeQuickInfo("received ID: "+id);
									break;
								case GET_MESSAGE:
									writeChatMessage(ca.message);
									break;
								
							}
							addToPool(ca);
							waiting(actionThread);
						}
						
				}

				public void waiting(Object o){
					synchronized(o){
						try
						{
							o.wait();
						}
						catch (InterruptedException e)
						{}
					}
				}
			});
	}

	public void scheduleAction(ControlAction ac)
	{
		schedule.add(ac);
		Log.d(TAG, "action scheduled");
		synchronized(actionThread){
			actionThread.notify();
		}
	}
	
	private void setID(int id)
	{
		client.ID = id;
		writeQuickInfo("ID SETTED");
	}
	
	private void connect(String serverIp, int serverPort)
	{
		Log.d(TAG,"try connecting");
		if (client.state.equals(TCPClient.State.DISCONNECTED))
		{ 
			Log.d(TAG,"is not connected");
			if (!serverIp.equals(""))
			{
				Log.d(TAG,"IP: " + serverIp); // bessere erkennung ob gültige ip
				client.serverIpAddress = serverIp;
				client.serverPort = serverPort;
				client.connect();
				writeQuickInfo("Connected");
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
		nc.setCommand(SEND_MESSAGE);
		nc.setMessage(m);
		client.scheduleCommand(nc);
	}
	
	private void writeChatMessage(String message)
	{
		guiAct.writeChatMessage(message);
	}
	
	private void writeQuickInfo(String m)
	{
		guiAct.writeQuickInfo(m);
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
