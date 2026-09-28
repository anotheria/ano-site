package net.anotheria.anosite.transfer;

import org.configureme.annotations.Configure;

import java.io.Serializable;

/**
 * One instance a document can be transferred to.
 *
 * <p>Part of {@link TransferTargetGroup}, configured in {@code anositeconfig.json} under its
 * {@code @targets} — see {@link TransferTargetGroup} for the shape and
 * {@link net.anotheria.anosite.shared.AnositeConfig#getTransferTargetGroups()} for the field.
 *
 * @author Leon Rosenberg
 */
public class TransferTarget implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the target, shown in the transfer dialog and in the result. Free text, only has to be
     * recognizable to the editors.
     */
    @Configure
    private String name;

    /**
     * Base url of the target's cms rest api, including the path the api is mapped under, for example
     * {@code https://cms-test1.example.com/api}. The engine appends the document's collection path and id to
     * it, so no trailing slash is needed — one is tolerated.
     */
    @Configure
    private String url;

    public String getName() {
        return name;
    }

    public void setName(final String aName) {
        this.name = aName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String aUrl) {
        this.url = aUrl;
    }

    /**
     * The configured url without its trailing slash, ready to have a path appended.
     *
     * @return base url of the target api, never ending in a slash
     */
    public String getNormalizedUrl() {
        if (url == null)
            return "";

        String normalized = url.trim();
        while (normalized.endsWith("/"))
            normalized = normalized.substring(0, normalized.length() - 1);

        return normalized;
    }

    /**
     * A target is only usable if it says where to transfer to.
     *
     * @return true if this target has a url
     */
    public boolean isValid() {
        return !getNormalizedUrl().isEmpty();
    }

    @Override
    public String toString() {
        return "TransferTarget{name='" + name + "', url='" + url + "'}";
    }
}
