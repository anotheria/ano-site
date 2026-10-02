package net.anotheria.anosite.transfer;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests how a transfer decides what to send.
 *
 * <p>Everything here is about {@link DocumentTransferService#plan(DocumentKey, TransferMode)} - the half that
 * walks the link graph. The other half is http against another instance and is not what breaks.
 */
public class DocumentTransferServiceTest {

    private static final String MODULE = "TestModule";
    private static final String OTHER_MODULE = "OtherModule";

    private TestSupport support;
    private DocumentTransferService service;

    @Before
    public void setUp() {
        support = new TestSupport(MODULE);
        TransferSupportRegistry.register(support);
        service = new DocumentTransferService();
    }

    @Test
    public void singleModeSendsOnlyTheSelectedDocument() throws Exception {
        support.document("Page", "1").links("Box", "10").links("Box", "11");
        support.document("Box", "10");
        support.document("Box", "11");

        TransferPlan plan = service.plan(key("Page", "1"), TransferMode.SINGLE);

        assertEquals(Arrays.asList(key("Page", "1")), plan.keys());
    }

    @Test
    public void deepModeSendsLinkedDocumentsBeforeTheDocumentLinkingThem() throws Exception {
        support.document("Page", "1").links("Box", "10");
        support.document("Box", "10").links("Bundle", "100");
        support.document("Bundle", "100");

        TransferPlan plan = service.plan(key("Page", "1"), TransferMode.DEEP);

        assertEquals(Arrays.asList(key("Bundle", "100"), key("Box", "10"), key("Page", "1")), plan.keys());
    }

    @Test
    public void deepModeSendsEveryDocumentOnce() throws Exception {
        support.document("Page", "1").links("Box", "10").links("Box", "11");
        support.document("Box", "10").links("Bundle", "100");
        support.document("Box", "11").links("Bundle", "100");
        support.document("Bundle", "100");

        TransferPlan plan = service.plan(key("Page", "1"), TransferMode.DEEP);

        assertEquals(4, plan.keys().size());
        assertEquals(1, plan.keys().stream().filter(k -> k.equals(key("Bundle", "100"))).count());
    }

    @Test
    public void deepModeSurvivesACycle() throws Exception {
        //a box whose sub box links back to it is ordinary cms content, not a broken document.
        support.document("Box", "10").links("Box", "11");
        support.document("Box", "11").links("Box", "10");

        TransferPlan plan = service.plan(key("Box", "10"), TransferMode.DEEP);

        assertEquals(2, plan.keys().size());
        assertTrue(plan.keys().contains(key("Box", "10")));
        assertTrue(plan.keys().contains(key("Box", "11")));
    }

    @Test
    public void aLinkThatCannotBeFollowedIsAWarningNotAFailure() throws Exception {
        support.document("Page", "1").links("Box", "gone");

        TransferPlan plan = service.plan(key("Page", "1"), TransferMode.DEEP);

        assertEquals(Arrays.asList(key("Page", "1")), plan.keys());
        assertEquals(1, plan.warnings().size());
        assertTrue(plan.warnings().get(0).contains("Box#gone"));
    }

    @Test
    public void aLinkIntoAModuleWithoutTransferSupportIsReported() throws Exception {
        support.document("Page", "1").linksTo(new DocumentKey("NotRegisteredModule", "Whatever", "7"));

        TransferPlan plan = service.plan(key("Page", "1"), TransferMode.DEEP);

        assertEquals(Arrays.asList(key("Page", "1")), plan.keys());
        assertEquals(1, plan.warnings().size());
        assertTrue(plan.warnings().get(0).contains("NotRegisteredModule"));
    }

    @Test
    public void deepModeCrossesModuleBorders() throws Exception {
        TestSupport other = new TestSupport(OTHER_MODULE);
        other.document("Bundle", "100");
        TransferSupportRegistry.register(other);

        support.document("Page", "1").linksTo(new DocumentKey(OTHER_MODULE, "Bundle", "100"));

        TransferPlan plan = service.plan(key("Page", "1"), TransferMode.DEEP);

        assertEquals(Arrays.asList(new DocumentKey(OTHER_MODULE, "Bundle", "100"), key("Page", "1")), plan.keys());
    }

    @Test
    public void referencedFilesTravelWithTheirDocument() throws Exception {
        //files used to be listed as a warning, as something an editor had to copy by hand afterwards. They
        //are uploaded with the document now, so planning has nothing to complain about - only an upload that
        //actually fails does, and that happens per target.
        support.document("Image", "5").withFile("logo.png");

        TransferPlan plan = service.plan(key("Image", "5"), TransferMode.SINGLE);

        assertTrue("files are not a warning anymore", plan.warnings().isEmpty());
        assertEquals(List.of("logo.png"), plan.documents().get(0).referencedFiles());
    }

    @Test
    public void theRestPathOfADocumentIsItsModuleAndTypeInLowerCase() {
        //the generated supports call this instead of carrying a baked in literal, and a deletion needs it
        //when the document itself is already gone.
        assertEquals("testmodule/localizationbundle", support.getRestPath("LocalizationBundle"));
    }

    @Test
    public void aTransferIsRefusedWhileTheInstanceIsNotAllowedToPublish() {
        //no anositeconfig in the test classpath, so transfer is off - which is the default an installation
        //that was not set up to publish runs with.
        try {
            service.transfer(key("Page", "1"), TransferMode.SINGLE, "test");
            fail("transferring from an instance with transfer disabled should fail");
        } catch (DocumentTransferException e) {
            assertTrue(e.getMessage().contains("not enabled"));
        }
    }

    @Test
    public void aDeletionIsRefusedWhileTheInstanceIsNotAllowedToPublish() {
        //the auto transfer deletes through the same engine, so the same switch has to hold it back.
        TransferTargetGroup group = new TransferTargetGroup();
        group.setName("test");
        group.setAutoTransfer(true);

        try {
            service.delete(key("Page", "1"), group);
            fail("deleting on a target from an instance with transfer disabled should fail");
        } catch (DocumentTransferException e) {
            assertTrue(e.getMessage().contains("not enabled"));
        }
    }

    @Test
    public void nothingIsAutoTransferredUntilAGroupAsksForIt() {
        assertTrue("an instance without configuration publishes nowhere by itself",
                service.getAutoTransferTargetGroups().isEmpty());
    }

    @Test
    public void anUnreadableSelectedDocumentStopsTheTransfer() {
        try {
            service.plan(key("Page", "does-not-exist"), TransferMode.SINGLE);
            fail("planning an unreadable document should fail");
        } catch (DocumentTransferException e) {
            assertTrue(e.getMessage().contains("does-not-exist"));
        }
    }

    @Test
    public void aModuleWithoutTransferSupportStopsTheTransfer() {
        try {
            service.plan(new DocumentKey("NotRegisteredModule", "Page", "1"), TransferMode.SINGLE);
            fail("planning a document of an unregistered module should fail");
        } catch (DocumentTransferException e) {
            assertTrue(e.getMessage().contains("NotRegisteredModule"));
        }
    }

    @Test
    public void unknownModesFallBackToTheSaferOne() {
        assertEquals(TransferMode.DEEP, TransferMode.fromParameter("deep"));
        assertEquals(TransferMode.SINGLE, TransferMode.fromParameter("single"));
        assertEquals(TransferMode.SINGLE, TransferMode.fromParameter("everything"));
        assertEquals(TransferMode.SINGLE, TransferMode.fromParameter(null));
    }

    @Test
    public void onlyGroupsWithATargetUrlAreOffered() {
        TransferTarget withUrl = new TransferTarget();
        withUrl.setName("test1");
        withUrl.setUrl("https://test1.example.com/api/");
        assertEquals("https://test1.example.com/api", withUrl.getNormalizedUrl());
        assertTrue(withUrl.isValid());

        TransferTarget withoutUrl = new TransferTarget();
        withoutUrl.setName("test2");
        assertFalse(withoutUrl.isValid());

        TransferTargetGroup usable = new TransferTargetGroup();
        usable.setName("test");
        usable.setTargets(new TransferTarget[]{withoutUrl, withUrl});
        assertTrue(usable.isValid());

        TransferTargetGroup unusable = new TransferTargetGroup();
        unusable.setName("prod");
        unusable.setTargets(new TransferTarget[]{withoutUrl});
        assertFalse(unusable.isValid());

        TransferTargetGroup unnamed = new TransferTargetGroup();
        unnamed.setTargets(new TransferTarget[]{withUrl});
        assertFalse(unnamed.isValid());
    }

    @Test
    public void aRejectedDocumentReportsWhatTheTargetSaid() {
        //a bare "HTTP 400" sent the last real failure to the maintainer instead of the editor; the body named
        //the offending field all along.
        String body = "{\"success\":false,\"message\":\"Unrecognized field \\\"definedParentName\\\"\"}";

        assertEquals(": " + body, DocumentTransferService.describeBody(body));
        assertEquals("", DocumentTransferService.describeBody(null));
        assertEquals("", DocumentTransferService.describeBody("   "));
    }

    @Test
    public void aRuntawayErrorBodyIsTrimmedForTheDialog() {
        String huge = "x".repeat(2000);

        String described = DocumentTransferService.describeBody(huge);

        assertTrue("the body should be trimmed", described.length() < huge.length());
        assertTrue("and say that it was", described.endsWith("..."));
    }

    private DocumentKey key(String documentName, String id) {
        return new DocumentKey(MODULE, documentName, id);
    }

    /**
     * A module whose documents are declared by the test instead of generated.
     */
    private static final class TestSupport extends AbstractModuleTransferSupport {

        private final String moduleName;
        private final Map<DocumentKey, TestDocument> documents = new HashMap<>();

        private TestSupport(String aModuleName) {
            this.moduleName = aModuleName;
        }

        private TestDocument document(String documentName, String id) {
            TestDocument document = new TestDocument(new DocumentKey(moduleName, documentName, id));
            documents.put(document.key, document);
            return document;
        }

        @Override
        public String getModuleName() {
            return moduleName;
        }

        @Override
        public List<String> getDocumentNames() {
            return documents.keySet().stream().map(DocumentKey::documentName).distinct().toList();
        }

        @Override
        public DocumentSnapshot load(String documentName, String id) throws Exception {
            DocumentKey key = new DocumentKey(moduleName, documentName, id);
            TestDocument document = documents.get(key);
            if (document == null)
                throw new IllegalStateException("No such document: " + key);

            return new DocumentSnapshot(key, getRestPath(documentName), "payload of " + key,
                    document.references, document.files);
        }
    }

    private static final class TestDocument {

        private final DocumentKey key;
        private final List<DocumentKey> references = new ArrayList<>();
        private final List<String> files = new ArrayList<>();

        private TestDocument(DocumentKey aKey) {
            this.key = aKey;
        }

        private TestDocument links(String documentName, String id) {
            return linksTo(new DocumentKey(key.moduleName(), documentName, id));
        }

        private TestDocument linksTo(DocumentKey target) {
            references.add(target);
            return this;
        }

        private TestDocument withFile(String fileName) {
            files.add(fileName);
            return this;
        }
    }
}
