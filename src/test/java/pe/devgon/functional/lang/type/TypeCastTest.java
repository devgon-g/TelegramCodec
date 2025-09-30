package pe.devgon.functional.lang.type;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pe.devgon.functional.lang.TypeCast;

import java.util.Date;
import java.util.function.Function;

@Slf4j
class TypeCastTest {

    @Test
    public void String_???????뮞??) {
        Function<String, Integer> function = new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return 1;
            }
        };

        String typeName = String.class.getSimpleName().toUpperCase();
        log.debug("String is [{}]", typeName);
        log.debug("TypeOf [{}]", TypeCast.of(String.class).apply(new Date()));
        log.debug("TypeOf [{}]", TypeCast.of(String.class).apply(10));
        log.debug("TypeOf [{}]", TypeCast.of(String.class).andThen(function).apply(10));
    }

    @Test
    public void testInt() {
        log.debug("TypeOf int [{}]", TypeCast.of(int.class).apply("10"));
        log.debug("TypeOf int [{}]", TypeCast.of(int.class).apply(8));
        log.debug("TypeOf int [{}]", TypeCast.of(Integer.class).apply(126.93));
    }

}