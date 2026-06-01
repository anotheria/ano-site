package net.anotheria.anosite.decorator;

import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asfederateddata.data.BoxType;
import net.anotheria.anosite.gen.asfederateddata.service.IASFederatedDataService;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.asg.util.decorators.IAttributeDecorator;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * This decorator decorates the attribute type of a box.
 * @author another
 *
 */
public class BoxTypeDecorator implements IAttributeDecorator{
	
	/**
	 * Federated data service to retrieve box type.
	 */
	private static IASFederatedDataService federatedDataService;


	static {
		try {
			federatedDataService = MetaFactory.get(IASFederatedDataService.class);
		} catch (MetaFactoryException e) {
			LoggerFactory.getLogger(BoxTypeDecorator.class).error(MarkerFactory.getMarker("FATAL"), "IASFederatedDataService asg service init failure", e);
		}
	}

	/* (non-Javadoc)
	 * @see net.anotheria.asg.util.decorators.IAttributeDecorator#decorate(net.anotheria.anodoc.data.Document, java.lang.String, java.lang.String)
	 */
	@Override public String decorate(DataObject doc, String attributeName, String rule) {
		String linkValue = ((Box)doc).getType();
		String name = null;
		try{
			BoxType targetType = federatedDataService.getBoxType(linkValue);
			name = targetType.getName();
		}catch(NoSuchDocumentException e){
			name = "*** DELETED ***";
		}catch(RuntimeException e){
			name = "*** ERR: "+e.getMessage()+ " ***";
		}catch(ASGRuntimeException e){
			name = "*** ASG-ERR: "+e.getMessage()+ " ***";
		}
		return name + " ["+linkValue+"]";
	}

}