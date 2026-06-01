package net.anotheria.anosite.decorator;

import net.anotheria.anodoc.data.Document;
import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anodoc.data.NoSuchPropertyException;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.assitedata.data.NaviItem;
import net.anotheria.anosite.gen.assitedata.data.Site;
import net.anotheria.anosite.gen.aswebdata.service.IASWebDataService;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.asg.util.decorators.IAttributeDecorator;
import net.anotheria.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.List;

public class NaviItemCMS2Decorator implements IAttributeDecorator{
	
	private static IASWebDataService service;

	/**
	 * Init.
	 */
	static {
		try {
			service = MetaFactory.get(IASWebDataService.class);
		} catch (MetaFactoryException e) {
          LoggerFactory.getLogger(NaviItemCMS2Decorator.class).error(MarkerFactory.getMarker("FATAL"), "IASSiteDataService  init failure", e);
		}
	}
	
	public String decorate(DataObject doc, String attributeName, String rule) {
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
			if (doc instanceof Site) {
				href = doc.getDefinedParentName().toLowerCase() + "Site" + StringUtils.capitalize(attributeName) + "Show?ownerId="
						+ doc.getId() +"&pId="+doc.getId()+"&ts=" + System.currentTimeMillis();
				// href = "site"+StringUtils.capitalize(attributeName)+"Show?pId="+doc.getId()+"&ts="+System.currentTimeMillis();
			}
			
			if (doc instanceof NaviItem) {
				href = doc.getDefinedParentName().toLowerCase() + "NaviItem" + StringUtils.capitalize(attributeName) + "Show?ownerId="
						+ doc.getId() +"&pId="+doc.getId()+ "&ts=" + System.currentTimeMillis();
				// href = "naviitem"+StringUtils.capitalize(attributeName)+"Show?pId="+doc.getId()+"&ts="+System.currentTimeMillis();
			}
			
			StringBuilder title = new StringBuilder();
			for (String id : ids){
				String name ;
				try{
					name = service.getPagex(id).getName();
				}catch(NoSuchDocumentException e){
					name = "*DELETED*";
				}catch(ASGRuntimeException e){
					name = "*ERR-"+e.getMessage()+"*";
				}
				if (title.length()>0)
					title.append(", ");
				title.append(name);
					
			}
			
			String titleString = "";
			if (title.length()>0)
				titleString = " title=\""+title.toString()+"\"";
			
			return "<a href=\""+href+"\""+titleString+">"+value+"</a>";
				
		}catch(NoSuchPropertyException e){
			return "none";
		}
	}

}
