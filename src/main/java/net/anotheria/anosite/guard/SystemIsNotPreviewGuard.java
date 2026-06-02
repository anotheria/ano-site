package net.anotheria.anosite.guard;

public class SystemIsNotPreviewGuard extends SystemIsPreviewGuard {

    @Override
    protected boolean shouldMatch() {
        return false;
    }

}
