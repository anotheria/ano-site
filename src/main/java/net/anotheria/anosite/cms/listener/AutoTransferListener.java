package net.anotheria.anosite.cms.listener;

import net.anotheria.anosite.access.AnoSiteAccessAPIImpl;
import net.anotheria.anosite.config.AutoTransfer;
import net.anotheria.anosite.config.AutoTransferConfig;
import net.anotheria.anosite.gen.asresourcedata.data.Image;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.listener.IServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Auto transfer content between configured modules.
 *
 * @author ykalapusha
 */
public abstract class AutoTransferListener implements IServiceListener {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnoSiteAccessAPIImpl.class);
    /**
     * Module filename.
     */
    private String filename;

    /**
     * Base constructor.
     *
     * @param moduleId - id of module data
     */
    public AutoTransferListener(String moduleId){
        filename = moduleId;
    }

    @Override
    public void documentUpdated(DataObject dataObject, DataObject dataObject1) {
        copyModule(dataObject1, false);
    }

    @Override
    public void documentDeleted(DataObject dataObject) {
        copyModule(dataObject, true);
    }

    @Override
    public void documentCreated(DataObject dataObject) {
        copyModule(dataObject, false);
    }

    @Override
    public void documentImported(DataObject dataObject) {
        copyModule(dataObject, false);
    }

    @Override
    public void persistenceChanged() {}

    private void copyModule(DataObject dataObject, boolean isDeleted) {
        AutoTransfer[] autoTransfers = AutoTransferConfig.getInstance().getAutoTransfers();
        if (autoTransfers != null) {
            for (AutoTransfer autoTransfer: autoTransfers) {
                //copy image file if needed
                if (dataObject instanceof Image)
                    processImageDocument((Image) dataObject, autoTransfer, isDeleted);

                String source = autoTransfer.getSourceDir() + "/content/" + filename + ".dat";
                String dest = autoTransfer.getCopyDir() + "/content/" + filename + ".dat";
                copyFiles(new File(source), new File(dest));
                File lockFile = new File(autoTransfer.getCopyDir() + "/content/locks/" + filename + ".lock");
                lockFile.delete();
            }
        }
    }


    private void processImageDocument(Image image, AutoTransfer autoTransfer, boolean isDeleted) {
        File imageSource = new File(autoTransfer.getSourceDir() + "/files/" + image.getImage());
        File imageDest = new File(autoTransfer.getCopyDir() + "/files/" + image.getImage());
        if (isDeleted) {
            imageDest.delete();
        } else {
            copyFiles(imageSource, imageDest);
        }
    }

    private void copyFiles(File source, File dest){
        try (InputStream is = new FileInputStream(source);
             OutputStream os  = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            LOGGER.error("Error while performing auto transfer. ", e);
        }
    }
}
