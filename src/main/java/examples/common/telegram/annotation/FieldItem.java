package examples.common.telegram.annotation;

import examples.common.lang.Pad;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldItem {
    int seq();
    int size();
    Pad pad() default Pad.RIGHT_SPACE;
}
