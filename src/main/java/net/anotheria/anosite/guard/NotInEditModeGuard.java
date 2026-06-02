package net.anotheria.anosite.guard;

public class NotInEditModeGuard extends InEditModeGuard{
	public boolean getDesiredResult(){
		return false;
	}
}
