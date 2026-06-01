package net.anotheria.anosite.decorator;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.asuserdata.service.IASUserDataService;
import net.anotheria.asg.exception.ASGRuntimeException;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * Decorator for roles list.
 * @author vbezuhlyi
 */
public class RolesListCMS2Decorator extends LinksListCMS2Decorator {

    /**
     * As site data service for script retrieval.
     */
    private static IASUserDataService service;

    /**
     * Init.
     */
    static {
        try {
            service = MetaFactory.get(IASUserDataService.class);
        } catch (MetaFactoryException e) {
            LoggerFactory.getLogger(ScriptsListCMS2Decorator.class).error(MarkerFactory.getMarker("FATAL"), "IASUserDataService  init failure", e);
        }
    }
    @Override
    protected String getLinkTargetName(String targetId) throws ASGRuntimeException {
        return service.getRoleDef(targetId).getName();
    }
}
