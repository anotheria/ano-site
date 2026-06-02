package net.anotheria.anosite.handler;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asfederateddata.data.BoxHandlerDef;
import net.anotheria.anosite.gen.asfederateddata.service.IASFederatedDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.HashMap;
import java.util.Map;

public class BoxHandlerFactory {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BoxHandlerFactory.class);
	
	private static IASFederatedDataService service;

	static {
		try {
			service = MetaFactory.get(IASFederatedDataService.class);
		} catch (MetaFactoryException e) {
			LOGGER.error(MarkerFactory.getMarker("FATAL"), "IASFederatedDataService init failure", e);
		}
	}
	
	private static Map<String, BoxHandlerProducer> producers = new HashMap<String, BoxHandlerProducer>();
	
	public static BoxHandler createHandler(String id){
		try{
			BoxHandlerDef def = service.getBoxHandlerDef(id);
		
			//first find producer
			String producerId = def.getClazz()+"-"+def.getId();
			BoxHandlerProducer producer = producers.get(producerId);
			if (producer==null){
				synchronized(producers){
					producer = producers.get(producerId);
					if (producer==null){
						producer = new BoxHandlerProducer(producerId);
						producers.put(producerId, producer);
					}
				}
			}
			//end producer lookup
			
			BoxHandlerWrapper wrapper = new BoxHandlerWrapper(producer, (BoxHandler)Class.forName(def.getClazz()).newInstance());
			return wrapper;
		
		}catch(Exception e){
			LOGGER.error("createHandler(" + id + ")", e);
			throw new RuntimeException("Handler instantiation failed - "+e.getMessage());
		}
		
	}
}
