package net.anotheria.anosite.content.servlet;

import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.shared.InternalResponseCode;

public class InternalBoxBeanResponse extends InternalResponse{
	private BoxBean bean;
	
	public InternalBoxBeanResponse(InternalResponseCode code, BoxBean aBean){
		super(code);
		bean = aBean;
	}
	
	public InternalBoxBeanResponse(BoxBean aBean){
		super(InternalResponseCode.CONTINUE);
		bean = aBean;
	}

	public BoxBean getBean(){
		return bean;
	}
}
