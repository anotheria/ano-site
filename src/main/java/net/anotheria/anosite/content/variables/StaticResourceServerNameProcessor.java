package net.anotheria.anosite.content.variables;

import net.anotheria.anosite.config.StaticResourceServerNameConfig;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Static resource server name processor.
 *
 * @author ykalapusha
 */
public class StaticResourceServerNameProcessor implements VariablesProcessor {

    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceServerNameProcessor.class);

    /**
     * Processor prefix.
     */
    public static final String PREFIX = "static";

    @Override
    public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
        if (StringUtils.isEmpty(variable)) {
            LOGGER.error("StaticResourceServerNameProcessor - replace method: Error! Missing key: " + variable + " in server " + req.getServerName() + " on page: " + req.getRequestURI());
            return null;
        }

        return getCorrectPathToStaticResource(variable);
    }

    /**
     * Get full path to the static resource.
     *
     * @param resource
     *      needed resource from static server
     * @return
     *      path to the static resource
     */
    private String getCorrectPathToStaticResource(final String resource) {
        String serverName = StaticResourceServerNameConfig.getInstance().getNameResourceServer();
        return serverName == null ? resource : serverName + resource;
    }
}
