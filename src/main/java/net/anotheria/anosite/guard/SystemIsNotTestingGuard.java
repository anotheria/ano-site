package net.anotheria.anosite.guard;

public class SystemIsNotTestingGuard extends SystemIsTestingGuard {

    @Override
    protected boolean shouldMatch() {
        return false;
    }

}
