package net.anotheria.anosite.cms.action.filehandling;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anotheria.asg.util.filestorage.FileStorage;
import net.anotheria.asg.util.filestorage.TemporaryFileHolder;
import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.maf.json.JSONResponse;
import net.anotheria.util.NumberUtils;
import net.anotheria.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Return info about file (size and dimension (for images) for now).
 */
public class ShowFileInfoAction extends BaseFileHandlingAction {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowFileInfoAction.class);


    @Override
    public ActionCommand execute(ActionMapping actionMapping, HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONResponse response = new JSONResponse();
        String fileName = req.getParameter("fileName");
        if (StringUtils.isEmpty(fileName) || fileName.split("\\.").length < 2) {
            LOGGER.warn("Unable to get info for file. File name is empty or has wrong format");
            response.addError("File name is empty.");
            writeTextToResponse(res, response);
            return null;
        }

        TemporaryFileHolder fileHolder = FileStorage.loadFile(fileName);
        if (fileHolder == null) {
            LOGGER.warn("Unable to get info for file. File not found.");
            response.addError("File not found.");
            writeTextToResponse(res, response);
            return null;
        }

        String size = NumberUtils.makeSizeString(fileHolder.getData().length) + " " + fileName;
        String[] elements = fileName.split("\\.");
        String extension = elements[elements.length - 1];
        String pixels = "";
        if (isImage(extension)) {
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileHolder.getData()));
                pixels = image.getWidth() + " x " + image.getHeight() + " pixel";
            } catch (Exception e) {
                LOGGER.warn("Unable to height and width of the file. {}", e.getMessage());
            }
        }

        JSONObject info = new JSONObject();
        info.put("size", size);
        info.put("pixels", pixels);
        response.setData(info);
        writeTextToResponse(res, response);
        return null;
    }

    private boolean isImage(String extension) {
        return "jpg".equals(extension) || "jpeg".equals(extension) || "png".equals(extension) || "gif".equals(extension)
                || "bmp".equals(extension) || "tif".equals(extension) || "tiff".equals(extension) || "svg".equals(extension)
                || "webp".equals(extension) || "heic".equals(extension) || "heif".equals(extension);
    }

    private void writeTextToResponse(final HttpServletResponse res, final JSONResponse jsonResponse) throws IOException, JSONException {
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        PrintWriter writer = res.getWriter();
        writer.write(jsonResponse.toString());
        writer.flush();
    }
}
