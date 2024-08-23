package net.anotheria.anosite.guard;

import jakarta.servlet.http.HttpServletRequest;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.api.configuration.SystemConfigurationAPI;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.exception.ASGRuntimeException;

/**
 * Abstract class that provides common methods and String constants for 
 * all derived guards.
 * 
 * @author Alex Osadchy
 *
 */
public abstract class SystemEnvironmentAbstractGuard implements ConditionalGuard {

    /**
     * System configuration API.
     */
    private SystemConfigurationAPI systemConfigurationAPI;

    /**
     * Constructor.
     */
    public SystemEnvironmentAbstractGuard() {
        systemConfigurationAPI = APIFinder.findAPI(SystemConfigurationAPI.class);
    }
    
    /**
     * Override this method to provide name of target environment.
     * 
     * @return name of the environment
     */
    protected abstract String getTargetEnvironmentName();
    
    /**
     * Override this method in a way that it would return true if 
     * environments should match, false otherwise.
     * 
     * @return boolean value.
     */
    protected abstract boolean shouldMatch();

    /**
     * Returns true if the condition is fullfilled.
     */
    @Override
    public final boolean isConditionFullfilled(DataObject object, HttpServletRequest req) throws ASGRuntimeException {
       return getEnvironmentName().startsWith(getTargetEnvironmentName().toUpperCase()) == shouldMatch();
    }
    
    /**
     * Gets environment name from API.
     * 
     * @return {@link String} with environment name
     */
    private String getEnvironmentName() {
        return systemConfigurationAPI.getCurrentSystem();
    }  
   
}
