package net.anotheria.anosite.content.servlet.resource.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadMeterOutput extends Thread{
	
	private long interval;
	
	private Logger log = LoggerFactory.getLogger(DownloadMeterOutput.class);
	
	public DownloadMeterOutput(long anInterval){
		super("DownloadMeterOutput");
		setDaemon(true);
		interval = anInterval;
		this.start();
	}
	
	public void run(){
		while(true){
			try{
				sleep(interval);
			}catch(InterruptedException e){}
			long amount = DownloadMeter.getDownloadedBytesInPeriod(interval);
			long kbit = amount / (interval/1000) * 8;
			log.info("Download amount in last "+interval+" milliseconds is : "+amount+" bytes, kbit: "+kbit);
		}
		
	}
}
