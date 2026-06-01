package net.anotheria.anosite.content.servlet.resource.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;



public class DownloadMeter {
	
	private static List<DownloadEntity> entities = new ArrayList<DownloadEntity>();
	private static final Object lock = new Object();
	
	private static Logger log = LoggerFactory.getLogger(DownloadMeter.class);
	
	private static long overallbytes = 0L;
	
	static{
		new DownloadMeterCleanupThread().start();
	}
	
	/**
	 * This offset signalizes that entries older than that interval are removed.
	 */
	public static final long REMOVE_OFFSET = 1000L*60*60*2;

	public static void addDownload(long bytes){
		synchronized (lock) {
			entities.add(new DownloadEntity(bytes));
			overallbytes += bytes;
		}
	}
	
	public static long getDownloadedBytesInPeriod(long period){
		long limit = System.currentTimeMillis() - period;
		long ret = 0;
		try{
			for (DownloadEntity e : entities){
				if (e.getTimestamp()>limit)
					ret += e.getBytes();
			}
		}catch(Exception e){}
		return ret;
	}
	
	public static long getDownloadKbitPerSecondInPeriod(long period){
		long downloaded = getDownloadedBytesInPeriod(period);
		long bitspersecond = 8 * downloaded / (period / 1000);
		long kbitpersecond = bitspersecond / 1000;
		return kbitpersecond;
		
	}
	
	static void cleanup(){
		ArrayList<DownloadEntity> newlist = new ArrayList<DownloadEntity>();
		long limit = System.currentTimeMillis() - REMOVE_OFFSET;
		int oldlistsize, newlistsize;
		long oldtotal, newtotal;
		oldtotal = newtotal = 0L;
		synchronized (lock) {
			oldlistsize = entities.size();
			for (DownloadEntity e : entities){
				oldtotal += e.getBytes();
				if (e.getTimestamp() > limit){
					newlist.add(e);
					newtotal += e.getBytes();
				}
			}
			entities = newlist;
			newlistsize = entities.size();
		}
		log.debug("DownloadMeter cleanup perfomed, old size: "+oldlistsize+", new size: "+newlistsize+", old total bytes: "+oldtotal+", new total bytes: "+newtotal+", overall: "+overallbytes);
	}
	
}
class DownloadEntity{
	private long timestamp;
	private long bytes;
	
	public DownloadEntity(long someBytes){
		bytes = someBytes;
		timestamp = System.currentTimeMillis();
	}
	
	public long getTimestamp(){
		return timestamp;
	}
	
	public long getBytes(){
		return bytes;
	}
}

class DownloadMeterCleanupThread extends Thread{
	
	public static final long SLEEP_INTERVAL = 1000L*60*15;
	
	private static Logger log = LoggerFactory.getLogger(DownloadMeter.class);
	
	DownloadMeterCleanupThread() {
		super("DownloadMeterCleanupThread");
		setDaemon(true);
	}
	
	public void run(){
		while(true){
			try{
				sleep(SLEEP_INTERVAL);
			}catch(InterruptedException e){}
			try{
				DownloadMeter.cleanup();
			}catch(Exception e){
				log.warn("Caught exception in cleanup!", e);
			}
		}
	}
}
