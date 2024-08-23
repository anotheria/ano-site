package net.anotheria.anosite.action.generic;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.anotheria.anosite.action.Action;
import net.anotheria.anosite.action.ActionCommand;
import net.anotheria.anosite.action.ActionMapping;

public class TestAction implements Action{

	public ActionCommand execute(HttpServletRequest req,
			HttpServletResponse responce, ActionMapping mapping)
			throws Exception {

		System.out.println("Called that action! "+this.getClass().getName()+", mapping: "+mapping);
		
		return mapping.getPredefinedCommand();
	}

}
