package pe.devgon.functional.lang.reflection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FieldsHelper {
    private static final Map<Class, FieldsHelper> helperMap = new HashMap<>();

    public static FieldsHelper of(Class type) {
        FieldsHelper helper = helperMap.get(type);
        return helper != null ? helper : load(type);
    }

    public static FieldsHelper load(Class type) {
        FieldsHelper helper = new FieldsHelper(type);
        helperMap.put(type, helper);
        return helper;
    }

    private final Field[] fields;

    private FieldsHelper(Class type) {
        fields = type.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> field.setAccessible(true));
    }

    public Stream<Field> all() {
        return Arrays.stream(fields);
    }

    public Stream<Field> annotationPresents(Class annotation) {
        return Arrays.stream(fields).filter(field -> field.isAnnotationPresent(annotation));
    }
}
