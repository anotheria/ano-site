package net.anotheria.anosite.guard;

import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.exception.ASGRuntimeException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Vlad Lukjanenko
 */
public class DisableTargetGuard implements ConditionalGuard {

    @Override
    public boolean isConditionFullfilled(DataObject object, HttpServletRequest req) throws ASGRuntimeException {
        return false;
    }
}