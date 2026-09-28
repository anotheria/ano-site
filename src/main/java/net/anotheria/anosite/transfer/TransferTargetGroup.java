package net.anotheria.anosite.transfer;

import org.configureme.annotations.Configure;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A named set of instances that are always transferred to together.
 *
 * <p>Editors pick a group, not a single machine: a test system is four nodes and the transfer has to reach all
 * of them, or the nodes disagree about the content. Groups are also what makes the standalone cms work — it is
 * not part of any of the systems it publishes to anymore, so "test" and "prod" are just two destinations it
 * knows by name.
 *
 * <p>Configured in {@code anositeconfig.json}. Note the {@code @} on both levels: it is configureme's marker
 * for an attribute holding objects rather than plain values, and without it the parser reads a json object as
 * an environment instead of a group - quietly, leaving the instance with no targets at all.
 * <pre>
 * "&#64;transferTargetGroups": [
 *     {
 *         "name": "test",
 *         "&#64;targets": [
 *             {"name": "test1", "url": "https://test1.example.com/api"},
 *             {"name": "test2", "url": "https://test2.example.com/api"}
 *         ]
 *     },
 *     {
 *         "name": "prod",
 *         "&#64;targets": [{"name": "prod1", "url": "https://www.example.com/api"}]
 *     }
 * ]
 * </pre>
 *
 * @author Leon Rosenberg
 */
public class TransferTargetGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the group. This is what the editor selects in the transfer dialog and what a transfer request
     * names, so it has to be unique within the configuration.
     */
    @Configure
    private String name;

    /**
     * The instances in this group. A transfer to the group goes to every one of them.
     */
    @Configure
    private TransferTarget[] targets = new TransferTarget[0];

    public String getName() {
        return name;
    }

    public void setName(final String aName) {
        this.name = aName;
    }

    public TransferTarget[] getTargets() {
        return targets;
    }

    public void setTargets(final TransferTarget[] aTargets) {
        this.targets = aTargets == null ? new TransferTarget[0] : aTargets;
    }

    /**
     * A group is only offered to editors if it has a name and at least one usable target.
     *
     * @return true if this group can be transferred to
     */
    public boolean isValid() {
        if (name == null || name.trim().isEmpty())
            return false;

        for (TransferTarget target : targets)
            if (target != null && target.isValid())
                return true;

        return false;
    }

    @Override
    public String toString() {
        return "TransferTargetGroup{name='" + name + "', targets=" + Arrays.toString(targets) + "}";
    }
}
