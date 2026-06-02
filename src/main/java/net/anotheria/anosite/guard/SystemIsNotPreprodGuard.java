package net.anotheria.anosite.guard;

public class SystemIsNotPreprodGuard extends SystemIsPreprodGuard {

    @Override
    protected boolean shouldMatch() {
        return false;
    }

}
