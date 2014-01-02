package de.lbl.kb_bachelor_app.control;
import de.lbl.kb_bachelor_app.control.ControlAction.*;

public class ControlAction
{
	public Actions action;

	public String serverIp;
	public int serverPort;

	public String message;

	public void setMessage(String message)
	{
		this.message = message;
	}

	public void setAction(ControlAction.Actions action)
	{
		this.action = action;
	}

	public void setPort(int serverPort)
	{
		this.serverPort = serverPort;
	}

	public void setIpAddress(String serverIpAddress)
	{
		this.serverIp = serverIpAddress;
	}
	public enum Actions{
		CONNECT,DISCONNECT,SEND,STOP
	}
}
