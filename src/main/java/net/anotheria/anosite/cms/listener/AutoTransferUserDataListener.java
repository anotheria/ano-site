package net.anotheria.anosite.cms.listener;

import net.anotheria.anosite.gen.asuserdata.data.ModuleASUserData;

public class AutoTransferUserDataListener extends AutoTransferListener {

    public AutoTransferUserDataListener() {
        super(ModuleASUserData.MODULE_ID);
    }
}
