package net.anotheria.anosite.cms.listener;

import net.anotheria.anosite.gen.asresourcedata.data.ModuleASResourceData;

/**
 * Auto transfer listener for Ano site resource data.
 *
 * @author ykalapusha
 */
public class AsResourceListener extends AutoTransferListener {

    public AsResourceListener() {
        super(ModuleASResourceData.MODULE_ID);
    }
}
