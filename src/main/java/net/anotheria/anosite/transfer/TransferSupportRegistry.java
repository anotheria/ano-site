package net.anotheria.anosite.transfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The module transfer supports this cms knows, by module name.
 *
 * <p>Filled at startup by the generated {@code TransferSupportRegistrar}, which the generated
 * {@code CMSMappingsConfigurator} calls — that is the one hook every cms runs before it serves a request.
 * Projects with hand written modules can add their own support with {@link #register(ModuleTransferSupport)}
 * from their context listener.
 *
 * <p>A module that is not registered is not an error: its documents simply cannot be transferred, and a deep
 * transfer that runs into a link pointing there reports it as skipped instead of failing.
 */
public final class TransferSupportRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(TransferSupportRegistry.class);

    /**
     * Supports by module name.
     */
    private static final Map<String, ModuleTransferSupport> SUPPORTS = new ConcurrentHashMap<>();

    private TransferSupportRegistry() {
    }

    /**
     * Adds a support to the registry, replacing whatever was registered for its module before.
     *
     * @param support support to add
     */
    public static void register(ModuleTransferSupport support) {
        if (support == null || support.getModuleName() == null)
            throw new IllegalArgumentException("support and its module name are required");

        ModuleTransferSupport previous = SUPPORTS.put(support.getModuleName(), support);
        if (previous != null && !previous.getClass().equals(support.getClass()))
            LOG.warn("Module {} registered twice for transfer, {} replaces {}", support.getModuleName(),
                    support.getClass().getName(), previous.getClass().getName());
    }

    /**
     * The support of a module.
     *
     * @param moduleName name of the module
     * @return the support, or null if that module cannot be transferred
     */
    public static ModuleTransferSupport get(String moduleName) {
        return moduleName == null ? null : SUPPORTS.get(moduleName);
    }

    /**
     * All registered supports.
     *
     * @return registered supports, in no particular order
     */
    public static Collection<ModuleTransferSupport> all() {
        return new ArrayList<>(SUPPORTS.values());
    }

    /**
     * Names of the modules that can be transferred.
     *
     * @return registered module names
     */
    public static List<String> moduleNames() {
        return new ArrayList<>(SUPPORTS.keySet());
    }
}
