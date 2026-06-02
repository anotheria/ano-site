package net.anotheria.anosite.decorator;

import net.anotheria.anodoc.data.Document;
import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anodoc.data.NoSuchPropertyException;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asfederateddata.service.ASFederatedDataServiceException;
import net.anotheria.anosite.gen.asfederateddata.service.IASFederatedDataService;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.gen.aswebdata.data.Pagex;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.decorators.IAttributeDecorator;
import net.anotheria.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorator for guards.
 * @author another
 *
 */
public class GuardDecorator implements IAttributeDecorator{
	/**
	 * Federated data service for guard def retrieval.
	 */
	/**
	 * Federated data service to retrieve box type.
	 */
	private static IASFederatedDataService service;


	static {
		try {
			service = MetaFactory.get(IASFederatedDataService.class);
		} catch (MetaFactoryException e) {
			LoggerFactory.getLogger(GuardDecorator.class).error(MarkerFactory.getMarker("FATAL"), "IASFederatedDataService asg service init failure", e);
		}
	}
	
	@Override public String decorate(DataObject doc, String attributeName, String rule) {
		try{
			
			//tmp hack
			List<Object> links = null;
			try{
				links = ((Document)doc).getListProperty(attributeName).getListData();
			}catch(NoSuchPropertyException e){
				links = new ArrayList<Object>(0);
			}
			List<String> ids = new ArrayList<String>(links.size());
			String value = ""+links.size()+" [";
			String linksValue = "";
			for (Object l : links){
				if (linksValue.length()>0)
					linksValue+=", ";
				linksValue+=l;
				ids.add(""+l);
			}
			value = value + linksValue+"]";
			
			String href = "#";
			
			if (doc instanceof Pagex){
				href = "pagex"+StringUtils.capitalize(attributeName)+"Show?pId="+doc.getId()+"&ownerId="+doc.getId()+"&ts="+System.currentTimeMillis();
			}
			if (doc instanceof Box){
				href = "aswebdataBoxGuardsShow?pId="+doc.getId()+"&ownerId="+doc.getId()+"&ts="+System.currentTimeMillis();
			}
			
			String title = "";
			for (String id : ids){
				String name ;
				try{
					name = service.getGuardDef(id).getName();
				}catch(NoSuchDocumentException e){
					name = "*DELETED*";
				}catch(ASFederatedDataServiceException e){
					name = "*ERR-"+e.getMessage()+"*";
				}
				if (title.length()>0)
					title += ", ";
				title += name;
					
			}
			if (title.length()>0)
				title = " title=\""+title+"\"";
			
			String hrefTarget = href.length()>1 ? " target=\"_blank\"" : "";
			
			return "<a href=\""+href+"\""+hrefTarget+title+">"+value+"</a>";
				
		}catch(NoSuchPropertyException e){
			return "none";
		}
	}

}
