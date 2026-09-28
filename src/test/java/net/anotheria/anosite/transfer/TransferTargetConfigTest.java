package net.anotheria.anosite.transfer;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Proves that the transfer target configuration documented in {@code HowToTransferDocumentsInAnosite.md} is
 * the configuration configureme actually reads.
 *
 * <p>This is here because getting it wrong is silent. Configureme marks attributes whose value is an object
 * with a leading {@code @}; without it a json object is read as an <i>environment</i> instead, the config
 * loads without complaint and the instance ends up with no transfer targets at all. A test that parses a real
 * file is the only thing that keeps the documented shape and the parsed shape from drifting apart.
 *
 * <p>It configures its own holder rather than {@link net.anotheria.anosite.shared.AnositeConfig}, whose
 * singleton is shared with everything else in the suite.
 */
public class TransferTargetConfigTest {

    @Test
    public void theDocumentedConfigurationParsesIntoGroupsAndTargets() {
        Holder holder = new Holder();
        ConfigurationManager.INSTANCE.configure(holder);

        assertTrue("transferEnabled should have been read", holder.transferEnabled);
        assertEquals("both groups should have been read", 2, holder.transferTargetGroups.length);

        TransferTargetGroup test = holder.transferTargetGroups[0];
        assertEquals("test", test.getName());
        assertTrue("a parsed group has to be usable", test.isValid());
        assertEquals("both targets of the group should have been read", 2, test.getTargets().length);
        assertEquals("test1", test.getTargets()[0].getName());
        assertEquals("https://test1.example.com/api", test.getTargets()[0].getUrl());

        //the api path is per target, not a constant: a group may mix /api and /asg-api installations.
        assertEquals("https://test2.example.com/asg-api", test.getTargets()[1].getNormalizedUrl());

        TransferTargetGroup prod = holder.transferTargetGroups[1];
        assertEquals("prod", prod.getName());
        assertEquals(1, prod.getTargets().length);
        assertEquals("the trailing slash should be trimmed off",
                "https://www.example.com/api", prod.getTargets()[0].getNormalizedUrl());
    }

    /**
     * Carries the same two fields as {@link net.anotheria.anosite.shared.AnositeConfig}, so what this test
     * parses is what the real config parses.
     */
    @ConfigureMe(name = "transfer-target-test-config")
    public static class Holder {

        @Configure
        private boolean transferEnabled = false;

        @Configure
        private TransferTargetGroup[] transferTargetGroups = new TransferTargetGroup[0];

        public void setTransferEnabled(final boolean aTransferEnabled) {
            this.transferEnabled = aTransferEnabled;
        }

        public void setTransferTargetGroups(final TransferTargetGroup[] aTransferTargetGroups) {
            this.transferTargetGroups = aTransferTargetGroups;
        }
    }
}
