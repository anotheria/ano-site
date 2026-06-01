package net.anotheria.anosite.guard;

public class SystemIsNotDevGuard extends SystemIsDevGuard {

    @Override
    protected boolean shouldMatch() {
        return false;
    }

}
