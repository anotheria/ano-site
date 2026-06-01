package net.anotheria.anosite.cms.helper;


import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anosite.gen.asfederateddata.data.BoxType;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.helper.cmsview.CMSViewHelper;

/**
 * BoxTypeBoxHelper class.
 */
public class BoxTypeBoxHelper extends BoxHelper implements CMSViewHelper{

	/**
	 * Returns Field explanation via String.
	 * @param documentPath path to doc
	 * @param object DataObject
	 * @param property property
	 * @return string field explanation
	 */
	public String getFieldExplanation(String documentPath, DataObject object, String property) {
		if (object==null)
			return null;
		Box box = (Box)object;
		String typeId = box.getType();
		if (typeId==null || typeId.length()==0)
			return null;
		try{
			BoxType type = getFederatedDataService().getBoxType(typeId);
			BoxSubHelper subHelper = getSubHelper(type.getName());
			return subHelper == null ? null : subHelper.getFieldExplanation(box, property);
		}catch(NoSuchDocumentException e){
			return "Box Type deleted!";
		}catch(Exception e){
			return "Error: "+e.getMessage();
		}
	}
}