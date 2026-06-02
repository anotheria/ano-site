package net.anotheria.anosite.guard;

public class SystemIsProdGuard extends SystemEnvironmentAbstractGuard {

    @Override
    protected String getTargetEnvironmentName() {
        return "prod";
    }

    @Override
    protected boolean shouldMatch() {
        return true;
    }
}
