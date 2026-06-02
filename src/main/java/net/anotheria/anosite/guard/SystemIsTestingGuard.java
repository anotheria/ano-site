package net.anotheria.anosite.guard;

public class SystemIsTestingGuard extends SystemEnvironmentAbstractGuard {

    @Override
    protected String getTargetEnvironmentName() {
        return "test_web01";
    }

    @Override
    protected boolean shouldMatch() {
        return true;
    }

}
