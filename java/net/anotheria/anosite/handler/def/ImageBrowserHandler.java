package net.anotheria.anosite.handler.def;

import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.content.bean.ImageResourceBean;
import net.anotheria.anosite.gen.asresourcedata.data.Image;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.handler.AbstractBoxHandler;
import net.anotheria.anosite.handler.BoxHandlerResponse;
import net.anotheria.anosite.handler.exception.BoxProcessException;
import net.anotheria.asg.exception.ASGRuntimeException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class ImageBrowserHandler extends AbstractBoxHandler{

	public static final String ATTR_AS_IMAGE_RESOURCES = "asImageResources";
	
	@Override
	public BoxHandlerResponse process(HttpServletRequest req, HttpServletResponse res, Box box, BoxBean bean) throws BoxProcessException {
		try {
			List<Image> imageResources = getResourceDataService().getImages();
			List<ImageResourceBean> imageResourceBeans = new ArrayList<ImageResourceBean>(imageResources.size());

			for (Image img : imageResources)
				imageResourceBeans.add(toImageResourceBean(req, img));

			req.setAttribute(ATTR_AS_IMAGE_RESOURCES, imageResourceBeans);
			return super.process(req, res, box, bean);
		} catch (ASGRuntimeException e) {
			throw new BoxProcessException(e);
		}
	}
	
	private ImageResourceBean toImageResourceBean(HttpServletRequest req, Image image){
		ImageResourceBean ret = new ImageResourceBean();
		ret.setId(image.getId());
		ret.setName(image.getName());
		ret.setTitle(image.getTitle());
		ret.setLink(req.getContextPath()+ "/file/" + image.getImage());
		return ret;
	}
	
}
