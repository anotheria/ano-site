package net.anotheria.anosite.cms.listener;

import net.anotheria.anosite.transfer.DocumentKey;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.data.ObjectInfo;
import net.anotheria.util.xml.XMLNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests what the auto transfer listener makes of a document event.
 *
 * <p>The listener itself does two things: it names the document the way the transfer engine does, and it
 * decides whether a save is worth publishing. Both are tested here; the publishing is the engine's and is
 * tested there.
 */
public class AutoTransferListenerTest {

    @Test
    public void aDocumentIsNamedByItsModuleItsTypeAndItsId() {
        //the old listener had to be told its module at construction time, one subclass per module. A document
        //carries all three, which is why one listener can serve every module.
        DocumentKey key = AutoTransferListener.key(new TestDocument("7", "footprint"));

        assertEquals(new DocumentKey("ASResourceData", "TextResource", "7"), key);
    }

    @Test
    public void aSaveThatChangedNothingIsNotPublished() {
        //this is what keeps an instance that is both a transfer target and configured to auto transfer from
        //bouncing a document back and forth forever: an incoming transfer writes what the sender has, so the
        //footprint of the new version equals the footprint of the old one.
        assertTrue(AutoTransferListener.unchanged(new TestDocument("7", "same"), new TestDocument("7", "same")));
    }

    @Test
    public void aSaveThatChangedTheContentIsPublished() {
        assertFalse(AutoTransferListener.unchanged(new TestDocument("7", "before"), new TestDocument("7", "after")));
    }

    @Test
    public void aSaveWithoutAPreviousVersionIsPublished() {
        //no old version means the service had no listeners when it read one, not that nothing changed.
        assertFalse(AutoTransferListener.unchanged(null, new TestDocument("7", "footprint")));
    }

    /**
     * A document that is nothing but an id and a footprint, which is all the listener looks at.
     */
    private static final class TestDocument implements DataObject {

        private final String id;
        private final String footprint;

        private TestDocument(String anId, String aFootprint) {
            this.id = anId;
            this.footprint = aFootprint;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public Object getPropertyValue(String propertyName) {
            return null;
        }

        @Override
        public String getDefinedName() {
            return "TextResource";
        }

        @Override
        public String getDefinedParentName() {
            return "ASResourceData";
        }

        @Override
        public XMLNode toXMLNode() {
            return new XMLNode("testdocument");
        }

        @Override
        public ObjectInfo getObjectInfo() {
            ObjectInfo info = new ObjectInfo();
            info.setId(id);
            info.setFootprint(footprint);
            return info;
        }
    }
}
