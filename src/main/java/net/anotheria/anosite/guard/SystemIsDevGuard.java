package net.anotheria.anosite.guard;

public class SystemIsDevGuard extends SystemEnvironmentAbstractGuard {

    @Override
    protected String getTargetEnvironmentName() {
        return "dev";
    }

    @Override
    protected boolean shouldMatch() {
        return true;
    }

}
