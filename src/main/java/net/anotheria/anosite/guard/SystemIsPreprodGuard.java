package net.anotheria.anosite.guard;

public class SystemIsPreprodGuard extends SystemEnvironmentAbstractGuard {

    @Override
    protected String getTargetEnvironmentName() {
        return "preprod";
    }

    @Override
    protected boolean shouldMatch() {
        return true;
    }

}
