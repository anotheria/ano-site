package net.anotheria.anosite.decorator;

import net.anotheria.anodoc.data.Document;
import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anodoc.data.NoSuchPropertyException;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.gen.aswebdata.data.Pagex;
import net.anotheria.anosite.gen.aswebdata.service.IASWebDataService;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.asg.util.decorators.IAttributeDecorator;
import net.anotheria.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorator for linked boxes.
 * @author lrosenberg
 */
public class BoxDecorator implements IAttributeDecorator{
	
	/**
	 * As web data service for box retrieval.
	 */
	/**
	 * Instance of webdataservice for retrieval of box definitions.
	 */
	private static IASWebDataService service;

	static {
		try {
			service = MetaFactory.get(IASWebDataService.class);
		} catch (MetaFactoryException e) {
			LoggerFactory.getLogger(BoxDecorator.class).error(MarkerFactory.getMarker("FATAL"), "IASWebDataService asg service init failure", e);
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
			if (doc instanceof Pagex) {
				href = doc.getDefinedParentName().toLowerCase() + StringUtils.capitalize(doc.getDefinedName())
						+ StringUtils.capitalize(attributeName) + "Show?pId=" + doc.getId() +"&ownerId="+doc.getId()+ "&ts=" + System.currentTimeMillis();
				// href = "pagex"+ StringUtils.capitalize(attributeName)+"Show?pId="+doc.getId()+"&ts="+System.currentTimeMillis();
			}
			if (doc instanceof Box) {
				href = doc.getDefinedParentName().toLowerCase() + "BoxSubboxesShow?pId=" + doc.getId() + "&ownerId="+doc.getId()+"&ts="
						+ System.currentTimeMillis();
				// href = "boxSubboxesShow?pId="+doc.getId()+"&ts="+System.currentTimeMillis();
			}
			
			String title = "";
			for (String id : ids){
				String name ;
				try{
					name = service.getBox(id).getName();
				}catch(NoSuchDocumentException e){
					name = "*DELETED*";
				}catch(ASGRuntimeException e){
					name = "*ASG-ERR: "+e.getMessage()+"*";
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
