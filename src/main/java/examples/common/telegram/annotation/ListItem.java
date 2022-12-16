package examples.common.telegram.annotation;

import examples.common.lang.Pad;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListItem {
    int countSeq() default 0;
    int countSize() default 0;
    Pad countPad() default Pad.RIGHT_SPACE;
    int dataSeq();
    int fixedCount() default 0;
}
