package de.lbl.kb_bachelor_app.network.gui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import de.lbl.kb_bachelor_app.R;
import de.lbl.kb_bachelor_app.control.ControlAction;
import de.lbl.kb_bachelor_app.control.ControlHandler;
import de.lbl.kb_bachelor_app.control.ControlListener;
import de.lbl.kb_bachelor_app.control.Controller;

public class ClientAct extends Activity implements ControlListener
{

	public final static String	TAG		= "ClientAct: ";

	private EditText				serverIpET;
	private EditText				serverPortET;
	private TextView				returnTextTV;
	private EditText				chatInputET;
	public TextView				quickInfoTV;

	// private Client client;
	public ControlHandler		ctrl;
	public Handler					handler	= new Handler();


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "create act");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);

		ctrl = new Controller(this);

		serverIpET = (EditText) findViewById(R.id.server_ip);
		serverPortET = (EditText) findViewById(R.id.server_port);

		returnTextTV = (TextView) findViewById(R.id.chat_log);
		chatInputET = (EditText) findViewById(R.id.chat_input);

		quickInfoTV = (TextView) findViewById(R.id.quickinfo);
		Log.d(TAG, "Client started");
	}


	public void connect(View v)
	{
		Log.d(TAG, "try connect");
		// get info
		String serverIpAddress = serverIpET.getText().toString();
		int serverPort = Integer.valueOf(serverPortET.getText().toString());
		// create action and store info
		ControlAction ac = ctrl.getNewAction();
		ac.setAction(ControlHandler.CONNECT);
		ac.setIpAddress(serverIpAddress);
		ac.setPort(serverPort);
		// try Action ;D
		tryAction(ac);
	}


	public void stop(View v)
	{
		Log.d(TAG, "try disconnect");
		ControlAction ac = ctrl.getNewAction();
		ac.setAction(ControlHandler.DISCONNECT);
		tryAction(ac);
	}


	public void send(View v)
	{
		Log.d(TAG, "try to send a message");
		String message = chatInputET.getText().toString();
		chatInputET.setText("");
		ControlAction ca = ctrl.getNewAction();
		ca.setAction(ControlHandler.SEND_MESSAGE);
		ca.setMessage(message);
		tryAction(ca);
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
	
	public void writeQuickInfo(String m)
	{
		final String me = m;
		handler.post(new Runnable(){
				@Override
				public void run(){
					quickInfoTV.setText(me);
				}
			});
	}
	
	public void writeChatMessage(String m)
	{
		final String me = returnTextTV.getText() +"\n"+ m;
		handler.post(new Runnable(){
				@Override
				public void run(){
					
					returnTextTV.setText(me);
				}
			});
	}
	
}
