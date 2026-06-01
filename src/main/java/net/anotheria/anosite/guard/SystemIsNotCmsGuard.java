package net.anotheria.anosite.guard;

public class SystemIsNotCmsGuard extends SystemIsCmsGuard {

    @Override
    protected boolean shouldMatch() {
        return false;
    }

}

