package net.anotheria.anosite.decorator;

import java.util.ArrayList;
import java.util.List;

import net.anotheria.anodoc.data.Document;
import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anodoc.data.NoSuchPropertyException;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.asg.util.decorators.IAttributeDecorator;
import net.anotheria.util.StringUtils;



/**
 * Basic Decorator for list of links
 * @author denis
 */
abstract public class LinksListDecorator implements IAttributeDecorator{
	
	@Override public String decorate(DataObject doc, String attributeName, String rule) {
		try{
			
			//tmp hack
			List<Object> links = null;
			try{
				links = ((Document)doc).getListProperty(attributeName).getListData();
			}catch(NoSuchPropertyException e){
				links = new ArrayList<Object>(0);
			}
			String value = ""+links.size()+" [";
			String linksValue = "";
			String title = "";
			for (Object l : links){
				String name ;
				try{
					name = getLinkTargetName("" + l);
				}catch(NoSuchDocumentException e){
					name = "*DELETED*";
				}catch(ASGRuntimeException e){
					name = "*ASG-ERR: "+e.getMessage()+"*";
				}
				if (title.length()>0)
					title += ", ";
				
				if (linksValue.length()>0)
					linksValue+=", ";
				linksValue+= name + "(" + l + ")";				
				title += name;
			}
			value = value + linksValue+"]";
			
			String href = doc.getDefinedParentName().toLowerCase() + StringUtils.capitalize(doc.getDefinedName())
			+ StringUtils.capitalize(attributeName) + "Show?pId=" + doc.getId() +"&ownerId="+doc.getId()+ "&ts=" + System.currentTimeMillis();
			
			
			if (title.length()>0)
				title = " title=\""+title+"\"";
			
			String hrefTarget = href.length()>1 ? " target=\"_blank\"" : "";
			
			return "<a href=\""+href+"\""+hrefTarget+title+">"+value+"</a>";
				
		}catch(NoSuchPropertyException e){
			return "none";
		}
	}
	
	abstract protected String getLinkTargetName(String targetId) throws ASGRuntimeException;

}
