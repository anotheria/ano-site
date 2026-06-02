package net.anotheria.anosite.content.servlet;

import java.util.List;

import net.anotheria.anosite.content.bean.BoxBean;
import net.anotheria.anosite.shared.InternalResponseCode;

public class InternalBoxBeanListResponse extends InternalResponse {
	private List<BoxBean> beans;
	
	public InternalBoxBeanListResponse(List<BoxBean> someBeans){
		this(InternalResponseCode.CONTINUE, someBeans);
	}
	
	public InternalBoxBeanListResponse(InternalResponseCode code, List<BoxBean> someBeans){
		super(code);
		beans = someBeans;
	}
	
	public List<BoxBean> getBeans(){
		return beans;
	}
}
