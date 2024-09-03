package net.anotheria.anosite.decorator;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.anotheria.anosite.gen.asresourcedata.data.Image;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.decorators.IAttributeDecorator;
import net.anotheria.util.IOUtils;
import net.anotheria.util.StringUtils;
import net.anotheria.webutils.filehandling.actions.FileStorage;
import net.anotheria.webutils.filehandling.beans.TemporaryFileHolder;

/**
 * This decorator looks up the linked image file on the disk and displays the size of the binary file.
 * @author lrosenberg
 *
 */
public class ImageSizeDecorator implements IAttributeDecorator {

	@Override
	public String decorate(DataObject doc, String attributeName, String rule) {
		if (doc instanceof Image)
			return processImage((Image)doc, attributeName, rule);
		return "";
	}

	private String processImage(Image img, String attributeName, String rule){
		String fileName = img.getImage();
		if (StringUtils.isEmpty(fileName))
			return "No file";

		String message = null;
		ImageInputStream iis = null;
		ByteArrayInputStream bais = null;

		try{
			TemporaryFileHolder f = FileStorage.loadFile(fileName);
			if (f == null){
				return "Missing " + fileName;
			}
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(fileName.substring(fileName.length()-3, fileName.length()));
			ImageReader reader = readers.next();
			bais = new ByteArrayInputStream(f.getData());
			iis = ImageIO.createImageInputStream(bais);
			reader.setInput(iis, false);
			int nImageCount = reader.getNumImages(true);
			if(nImageCount<1){
				return "Error: ImageReader found no images";
			}
			int h = reader.getHeight(0);
			int w = reader.getWidth(0);

			message = w+" x "+h+" pixel";
		}catch(Exception e){
			message = "Error: "+e.getMessage();
		}finally{
			IOUtils.closeIgnoringException(iis);
			IOUtils.closeIgnoringException(bais);
		}
		return message;
	}

}
