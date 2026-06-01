package net.anotheria.anosite.guard;

public class SystemIsPreviewGuard extends SystemEnvironmentAbstractGuard {

    @Override
    protected String getTargetEnvironmentName() {
        return "test_preview";
    }

    @Override
    protected boolean shouldMatch() {
        return true;
    }

}
