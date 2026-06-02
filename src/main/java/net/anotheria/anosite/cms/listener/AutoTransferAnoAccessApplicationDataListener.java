package net.anotheria.anosite.cms.listener;

import net.anotheria.anosite.gen.anoaccessapplicationdata.data.ModuleAnoAccessApplicationData;

public class AutoTransferAnoAccessApplicationDataListener extends AutoTransferListener {

    public AutoTransferAnoAccessApplicationDataListener() {
        super(ModuleAnoAccessApplicationData.MODULE_ID);
    }
}
