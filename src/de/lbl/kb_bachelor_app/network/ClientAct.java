package de.lbl.kb_bachelor_app.network;


import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import de.lbl.kb_bachelor_app.*;


public class ClientAct extends Activity
{

	public final static String TAG = "ClientAct: ";
	
    private EditText serverIpET;
	private EditText serverPortET;
    private TextView returnTextTV;
	private EditText chatInputET;
	public TextView quickInfoTV;

	//private Client client;
	public Controller ctrl;
    public Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG,"create act");
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
		Log.d(TAG,"try connect");
		//get info
		String serverIpAddress = serverIpET.getText().toString();
		int serverPort = Integer.valueOf(serverPortET.getText().toString());
		//create action and store info
		ControlAction ac = ctrl.getNewAction();
		ac.setAction(ControlAction.Actions.CONNECT);
		ac.setIpAddress(serverIpAddress);
		ac.setPort(serverPort);
		//try Action ;D
		ctrl.scheduleAction(ac);
	}



	public void stop(View v)
	{
		Log.d(TAG,"try disconnect");
		ControlAction ac = ctrl.getNewAction();
		ac.setAction(ControlAction.Actions.STOP);
		ctrl.scheduleAction(ac);
	}

	public void send(View v)
	{
		Log.d(TAG,"try to send a message");
		String message = chatInputET.getText().toString();
		ControlAction ac = ctrl.getNewAction();
		ac.setAction(ControlAction.Actions.SEND);
		ac.setMessage(message);
		ctrl.scheduleAction(ac);
	}
}
