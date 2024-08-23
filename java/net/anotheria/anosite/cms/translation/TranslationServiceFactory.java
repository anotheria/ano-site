package net.anotheria.anosite.cms.translation;

import net.anotheria.anoprise.metafactory.ServiceFactory;
import net.anotheria.asg.service.ASGService;
import net.anotheria.moskito.core.dynamic.MoskitoInvokationProxy;
import net.anotheria.moskito.core.predefined.ServiceStatsCallHandler;
import net.anotheria.moskito.core.predefined.ServiceStatsFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class TranslationServiceFactory implements ServiceFactory<IASGTranslationService> {

    private static AtomicInteger instanceCounter = new AtomicInteger(0);
    private static IASGTranslationService defaultInstance = createASActionService();

    public IASGTranslationService create() {
        return createASActionService();
    }

    public static IASGTranslationService createASActionService() {
        MoskitoInvokationProxy proxy = new MoskitoInvokationProxy(
                createInstance(),
                new ServiceStatsCallHandler(),
                new ServiceStatsFactory(),
                "IASGTranslationService-" + instanceCounter.incrementAndGet(),
                "service",
                "asg-fed",
                IASGTranslationService.class, ASGService.class
        );
        return (IASGTranslationService) proxy.createProxy();
    }

    private static IASGTranslationService createInstance() {
        return IASGTranslationTranslationServiceImpl.getInstance();
    }

    static IASGTranslationService getDefaultInstance() {
        return defaultInstance;
    }

}
