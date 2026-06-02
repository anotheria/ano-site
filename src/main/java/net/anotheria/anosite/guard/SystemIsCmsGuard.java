package net.anotheria.anosite.guard;

public class SystemIsCmsGuard extends SystemEnvironmentAbstractGuard {

    @Override
    protected String getTargetEnvironmentName() {
        return "cms";
    }

    @Override
    protected boolean shouldMatch() {
        return true;
    }

}
