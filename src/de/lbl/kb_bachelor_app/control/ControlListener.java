package de.lbl.kb_bachelor_app.control;

public interface ControlListener
{
	public void tryAction(ControlAction ca);
	public void addController(ControlHandler ah);
}
