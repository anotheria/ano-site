package net.anotheria.anosite.config;

import org.configureme.annotations.Configure;

import java.io.Serializable;

/**
 * Auto transfer config data.
 *
 * @author ykalapusha
 */
public class AutoTransfer implements Serializable {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 430324017527360792L;
    /**
     * Source directory, must be a full path.
     */
    @Configure
    private String sourceDir;
    /**
     * Directory to copy asg-modules, must be a full path.
     */
    @Configure
    private String copyDir;

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getCopyDir() {
        return copyDir;
    }

    public void setCopyDir(String copyDir) {
        this.copyDir = copyDir;
    }

    @Override
    public String toString() {
        return "AutoTransfer{" +
                "sourceDir='" + sourceDir + '\'' +
                ", copyDir='" + copyDir + '\'' +
                '}';
    }
}
