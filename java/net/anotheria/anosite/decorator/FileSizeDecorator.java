package net.anotheria.anosite.decorator;

import java.io.ByteArrayInputStream;

import net.anotheria.anosite.gen.asresourcedata.data.FileLink;
import net.anotheria.anosite.gen.asresourcedata.data.Image;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.decorators.IAttributeDecorator;
import net.anotheria.util.IOUtils;
import net.anotheria.util.NumberUtils;
import net.anotheria.util.StringUtils;
import net.anotheria.webutils.filehandling.actions.FileStorage;
import net.anotheria.webutils.filehandling.beans.TemporaryFileHolder;


/**
 * This decorator returns the size of the underlying ImageDocument and the contained image file name.
 * @author another
 *
 */
public class FileSizeDecorator implements IAttributeDecorator{
	
	@Override public String decorate(DataObject doc, String attributeName, String rule) {
		if (doc instanceof Image)
			return processImage((Image)doc, attributeName, rule);
		if (doc instanceof FileLink)
			return processFile((FileLink)doc, attributeName, rule);
		return "";
	}
	
	private String processImage(Image img, String attributeName, String rule){
		String fileName = img.getImage();
		return processFile(fileName);
	}
	
	private String processFile(FileLink f, String attributeName, String rule){
		String fileName = f.getFile();
		return processFile(fileName);
	}

	private String processFile(String fileName){
		if (StringUtils.isEmpty(fileName))
			return "No file";

		String message;
		ByteArrayInputStream bais  = null;
		try{
			TemporaryFileHolder f = FileStorage.loadFile(fileName);
			if (f == null) {
				message = "Missing "+fileName;
			}else{
				bais = new ByteArrayInputStream(f.getData());
				message = NumberUtils.makeSizeString(bais.available()) + " " + fileName;
			}
		}catch(Exception e){
			message = "Error: "+e.getMessage();
		}finally{
			IOUtils.closeIgnoringException(bais);
		}
		return message;
	}
}

