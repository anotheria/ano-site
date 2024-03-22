package net.anotheria.anosite.handler;

import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.util.ModelObjectMapper;
import net.anotheria.anosite.handler.exception.BoxProcessException;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoxHandlerWrapper implements BoxHandler{

	private BoxHandlerProducer producer;
	private BoxHandler handler;

	public BoxHandlerWrapper(BoxHandlerProducer aProducer, BoxHandler aHandler){
		producer = aProducer;
		handler = aHandler;
	}
	
	public BoxHandlerResponse process(HttpServletRequest req,
			HttpServletResponse res, Box box, BoxBean bean)
			throws BoxProcessException {

		ModelObjectMapper.map(req, handler);
		return producer.process(req, res, box, bean, handler);
	}

	public BoxHandlerResponse submit(HttpServletRequest req,
			HttpServletResponse res, Box box) throws BoxProcessException {
		ModelObjectMapper.map(req, handler);
		/*ModelObjectMapper.validate(req, handler);*/
		return producer.submit(req, res, box, handler);
	}
	
}
