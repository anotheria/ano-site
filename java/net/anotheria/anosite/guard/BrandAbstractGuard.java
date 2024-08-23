package net.anotheria.anosite.guard;

import net.anotheria.anodoc.util.context.ContextManager;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.exception.ASGRuntimeException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Abstract brand guard.
 *
 * @author ykalapusha
 */
public abstract class BrandAbstractGuard implements ConditionalGuard {
    /**
     * Target brand.
     *
     * @return {@link String} brand target name
     */
    protected abstract String getTargetBrand();

    /**
     * By overriding this method we can change conditional logic.
     *
     * @return {@code true} if target brand should be equal to current logic or {@code false} if shouldn't
     */
    protected boolean shouldMatch() {
        return true;
    }

    @Override
    public boolean isConditionFullfilled(DataObject object, HttpServletRequest req) throws ASGRuntimeException {
        return shouldMatch() == getTargetBrand().equals(ContextManager.getCallContext().getBrandConfig().getName());
    }
}
