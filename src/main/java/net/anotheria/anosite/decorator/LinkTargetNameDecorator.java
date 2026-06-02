package net.anotheria.anosite.decorator;

import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anodoc.data.NoSuchPropertyException;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asfederateddata.service.IASFederatedDataService;
import net.anotheria.anosite.gen.aslayoutdata.service.IASLayoutDataService;
import net.anotheria.anosite.gen.assitedata.data.EntryPoint;
import net.anotheria.anosite.gen.assitedata.data.NaviItem;
import net.anotheria.anosite.gen.assitedata.data.PageAlias;
import net.anotheria.anosite.gen.assitedata.data.PageTemplate;
import net.anotheria.anosite.gen.assitedata.data.Site;
import net.anotheria.anosite.gen.assitedata.service.IASSiteDataService;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.gen.aswebdata.data.Pagex;
import net.anotheria.anosite.gen.aswebdata.service.IASWebDataService;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.exception.ASGRuntimeException;
import net.anotheria.asg.util.decorators.IAttributeDecorator;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * This attribute decorator decorates known anosite objects as links to the linked documents (instead of id).
 * @author another
 *
 */
public class LinkTargetNameDecorator implements IAttributeDecorator{
	/**
	 * Federated data service for guard, boxtypes and handlers.
	 */
	private static IASFederatedDataService federatedDataService;
	/**
	 * Sitedata service for sites, templates etc.
	 */
	private static IASSiteDataService siteDataService;
	/**
	 * WebData service for pagexs and boxes.
	 */
	private static IASWebDataService webDataService;
	/**
	 * Layout data service for layouts and styles.
	 */
	private static IASLayoutDataService layoutDataService;

	static {
		try {
			federatedDataService = MetaFactory.get(IASFederatedDataService.class);
			siteDataService = MetaFactory.get(IASSiteDataService.class);
			webDataService = MetaFactory.get(IASWebDataService.class);
			layoutDataService = MetaFactory.get(IASLayoutDataService.class);
		} catch (MetaFactoryException e) {
         	LoggerFactory.getLogger(LinkTargetNameDecorator.class).error(MarkerFactory.getMarker("FATAL"), "ASG service init failure", e);
		}
	}

	@Override public String decorate(DataObject doc, String attributeName, String rule) {
		String name = "Unknown";
		String linkValue = "?";
		try{
			Object link = doc.getPropertyValue(attributeName);
			
			linkValue = link == null ? null : ""+link;
			if (linkValue==null || linkValue.length()==0)
				return "----";
			if (doc instanceof PageTemplate){
				name = getTargetNameForTemplate((PageTemplate)doc, attributeName);
			}
			if (doc instanceof NaviItem){
				name = getTargetNameForNaviItem((NaviItem)doc, attributeName);
			}
			if (doc instanceof Box){
				name = getTargetNameForBox((Box)doc, attributeName);
			}
			if (doc instanceof Pagex){
				name = getTargetNameForPage((Pagex)doc, attributeName);
			}
			if (doc instanceof Site){
				name = getTargetNameForSite((Site)doc, attributeName);
			}
			if (doc instanceof EntryPoint){
				name = getTargetNameForEntryPoint((EntryPoint)doc, attributeName);
			}
			if (doc instanceof PageAlias){
				name = getTargetNameForPageAlias((PageAlias)doc, attributeName);
			}
		}catch(NoSuchDocumentException e){
			name = "*** DELETED ***";
		}catch(NoSuchPropertyException e){
			name = "*** NoProp ***";
		}catch(RuntimeException e){
			name = "*** ERR: "+e.getMessage()+" ***";
		}catch(ASGRuntimeException e){
			name = "*** ASG-ERR: "+e.getMessage()+" ***";
		}

		return name + " ["+linkValue+"]";
	}
	
	/**
	 * Returns the name of the linked document for navi item document.
	 * @param item
	 * @param attributeName
	 * @return
	 * @throws ASGRuntimeException
	 */
	private String getTargetNameForNaviItem(NaviItem item, String attributeName) throws ASGRuntimeException{
		if (attributeName.equals("internalLink")){
			return webDataService.getPagex(item.getInternalLink()).getName();
		}
		if (attributeName.equals("pageAlias")) {
			return siteDataService.getPageAlias(item.getPageAlias()).getName();
		}
		return "Unknown attribute: "+attributeName;
		
	}
	
	/**
	 * Returns the name of the linked document for template document.
	 * @param template
	 * @param attributeName
	 * @return
	 * @throws ASGRuntimeException
	 */
	private String getTargetNameForTemplate(PageTemplate template, String attributeName) throws ASGRuntimeException{
		if (attributeName.equals("site")){
			return siteDataService.getSite(template.getSite()).getName();
		}
		if (attributeName.equals("layout")){
			return layoutDataService.getPageLayout(template.getLayout()).getName();
		}
		return "Unknown attribute: "+attributeName;
	}
	
	private String getTargetNameForSite(Site site, String attributeName) throws ASGRuntimeException{
		if (attributeName.equals("startpage")){
			return webDataService.getPagex(site.getStartpage()).getName();
		}
		return "Unknown attribute: "+attributeName;
	}

	/**
	 * Returns the name of the linked document for EntryPoint.
	 * @param entry
	 * @param attributeName
	 * @return
	 * @throws ASGRuntimeException
	 */
	private String getTargetNameForEntryPoint(EntryPoint entry, String attributeName)throws ASGRuntimeException{
		if (attributeName.equals("startPage")){
			return webDataService.getPagex(entry.getStartPage()).getName();
		}
		
		if (attributeName.equals("startSite")){
			return siteDataService.getSite(entry.getStartSite()).getName();
		}
		
		return "Unknown attribute: "+attributeName;
	}

	/**
	 * Returns the name of the linked document for page alias.
	 * @param entry
	 * @param attributeName
	 * @return
	 * @throws ASGRuntimeException
	 */
	private String getTargetNameForPageAlias(PageAlias entry, String attributeName)throws ASGRuntimeException{
		if (attributeName.equals("targetPage")){
			return webDataService.getPagex(entry.getTargetPage()).getName();
		}
		return "Unknown attribute: "+attributeName;
	}

	/**
	 * Returns the name of the linked document for boxes.
	 * @param box
	 * @param attributeName
	 * @return
	 * @throws ASGRuntimeException
	 */
	private String getTargetNameForBox(Box box, String attributeName)throws ASGRuntimeException{
		if (attributeName.equals("handler")){
			return federatedDataService.getBoxHandlerDef(box.getHandler()).getName();
		}
		if (attributeName.equals("type")){
			return federatedDataService.getBoxType(box.getType()).getName();
		}
		return "UnknownAttr: "+attributeName;
	}

	/**
	 * Returns the name of the linked document for pagexs.
	 * @param page
	 * @param attributeName
	 * @return
	 * @throws ASGRuntimeException
	 */
	private String getTargetNameForPage(Pagex page, String attributeName)throws ASGRuntimeException{
		if (attributeName.equals("template")){
			return siteDataService.getPageTemplate(page.getTemplate()).getName();
		}
		return "UnknownAttr: "+attributeName;
	}
	
}
