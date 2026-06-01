package test;

import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.listener.ServiceListenerAdapter;

public class TestListener extends ServiceListenerAdapter{

	@Override
	public void documentCreated(DataObject doc) {
		System.out.println("SDELAN DOKUMENT "+doc);
	}

	@Override
	public void documentDeleted(DataObject doc) {
		System.out.println("DOKUMENT  NAHUJ "+doc);
	}
	
}
