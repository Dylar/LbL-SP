package de.lbl.kb_bachelor_app.control;

public interface ControlHandler
{

	public static final int	SEND_ID			= 0;
	public static final int	SEND_MESSAGE	= 1;
	public static final int	GET_MESSAGE		= 4;
	public static final int	START_SERVER	= 2;
	public static final int	STOP_SERVER		= 3;
	public static final int	DISCONNECT		= 5;
	public static final int	CONNECT			= 6;


	public void scheduleAction(ControlAction ca);


	public ControlAction getNewAction();

}
