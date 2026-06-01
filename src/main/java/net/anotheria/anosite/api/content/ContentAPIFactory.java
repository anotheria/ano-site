package net.anotheria.anosite.api.content;

import net.anotheria.anoplass.api.APIFactory;

public class ContentAPIFactory implements APIFactory<ContentAPI>{

	@Override public ContentAPI createAPI() {
		return new ContentAPIImpl();
	}

}
