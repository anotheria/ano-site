package net.anotheria.anosite.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.anotheria.anosite.gen.anoaccessconfiguration.rest.RoleVO;
import net.anotheria.anosite.gen.aswebdata.rest.PagexVO;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * A rest VO has to survive its own round trip.
 *
 * <p>That is the whole of a document transfer: the source serializes a VO, the target deserializes the same
 * json back into the same VO class. It sounds tautological and it was not — the VO implements
 * {@code DataObject}, whose {@code getDefinedName()}, {@code getDefinedParentName()} and
 * {@code getObjectInfo()} are getters with no setters behind them. Jackson happily wrote them out and then
 * refused to read them back with {@code UnrecognizedPropertyException}, so every transfer of every document
 * type failed with a 400 on the target while the url and the configuration were perfectly fine.
 *
 * <p>The stubs are {@code @JsonIgnore}d now. This test is here so nobody adds another getter to the VO
 * without noticing what it costs.
 */
public class RestVoRoundTripTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void aVoSerializesToSomethingItCanReadBack() throws Exception {
        RoleVO vo = new RoleVO();
        vo.setId("1");
        vo.setName("editor");
        vo.setDescription("may edit");
        vo.setPermissions(Arrays.asList("p1", "p2"));

        String json = mapper.writeValueAsString(vo);
        RoleVO back = mapper.readValue(json, RoleVO.class);

        assertEquals("1", back.getId());
        assertEquals("editor", back.getName());
        assertEquals("may edit", back.getDescription());
        assertEquals(Arrays.asList("p1", "p2"), back.getPermissions());
    }

    @Test
    public void theDataObjectStubsStayOutOfTheJson() throws Exception {
        String json = mapper.writeValueAsString(RoleVO.class.getDeclaredConstructor().newInstance());

        assertFalse("definedName is not content: " + json, json.contains("definedName"));
        assertFalse("definedParentName is not content: " + json, json.contains("definedParentName"));
        assertFalse("objectInfo is not content: " + json, json.contains("objectInfo"));
    }

    @Test
    public void aDocumentWithListsAndLinksRoundTripsToo() throws Exception {
        PagexVO vo = new PagexVO();
        vo.setId("7");
        vo.setName("start");
        vo.setC1(Arrays.asList("box1", "box2"));
        vo.setLocalizations(Arrays.asList("bundle1"));
        vo.setTemplate("template1");

        PagexVO back = mapper.readValue(mapper.writeValueAsString(vo), PagexVO.class);

        assertEquals("7", back.getId());
        assertEquals("start", back.getName());
        assertEquals(Arrays.asList("box1", "box2"), back.getC1());
        assertEquals(Arrays.asList("bundle1"), back.getLocalizations());
        assertEquals("template1", back.getTemplate());
    }

    @Test
    public void aFieldTheReceiverDoesNotKnowIsIgnoredRatherThanFatal() throws Exception {
        //source and target are not always on the same build; a field added on one side must not reject the
        //whole document on the other.
        String fromANewerSource = "{\"id\":\"1\",\"name\":\"editor\",\"fieldFromTheFuture\":\"whatever\"}";

        RoleVO back = mapper.readValue(fromANewerSource, RoleVO.class);

        assertEquals("1", back.getId());
        assertEquals("editor", back.getName());
    }
}
