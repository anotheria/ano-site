package net.anotheria.anosite.cms.translation;

import net.anotheria.asg.service.ASGService;

public interface IASGTranslationService extends ASGService {

    String translate(String originLanguage, String targetLanguage, String bundleContent);

}
