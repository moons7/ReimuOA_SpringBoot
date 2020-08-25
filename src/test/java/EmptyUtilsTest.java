import com.reimu.util.EmptyUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class EmptyUtilsTest {

    @Test
    public void emptyTest() {
        Object nullObject = null;
        assertTrue(EmptyUtils.isEmpty(new HashMap()),"error Empty map");
        assertTrue(EmptyUtils.isEmpty(new ArrayList()),"error Empty list");
        assertTrue(EmptyUtils.isEmpty(""),"error Empty string");
        assertTrue(EmptyUtils.isEmpty(nullObject),"error Empty string");
        assertFalse(EmptyUtils.isEmpty(new Object()),"error Empty object");
        assertFalse(EmptyUtils.isEmpty(new Object(),true),"error Empty object");
        assertFalse(EmptyUtils.isEmpty(new Object(),false),"error Empty object");
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            EmptyUtils.isEmpty(nullObject,false);
        });
        assertEquals(exception.toString(),"java.lang.NullPointerException");
    }
}
