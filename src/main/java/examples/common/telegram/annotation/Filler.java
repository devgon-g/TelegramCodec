package examples.common.telegram.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Fillers.class)
public @interface Filler {
    int seq();
    int size();
    char fillerChar() default ' ';
}
