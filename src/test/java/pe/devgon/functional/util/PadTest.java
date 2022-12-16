package pe.devgon.functional.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pe.devgon.functional.lang.TypeCast;

import java.util.function.Function;

@Slf4j
class PadTest {
    @Test
    public void test() {
        Function cast = TypeCast.of(String.class).andThen(Pad.LEFT_ZERO.size(10));
        log.debug("left zero - {}", cast.apply(12));
        log.debug("left zero - {}", Pad.LEFT_ZERO.size(5).apply("123"));
    }

}