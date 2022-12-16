package pe.devgon;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private String id;
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws NoSuchFieldException {
        Field field = this.getClass().getDeclaredField("id");

        field.getType();
//        assertTrue( true );
    }
}
