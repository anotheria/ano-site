package net.anotheria.anosite.guard;

public class SystemIsNotProdGuard extends SystemIsProdGuard {

    @Override
    protected boolean shouldMatch() {
        return false;
    }

}
