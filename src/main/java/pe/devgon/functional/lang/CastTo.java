package pe.devgon.functional.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class CastTo<T, R> implements Function<T, R> {
    private static final Map<Class<?>, Function<String, ?>> funtionMap = new HashMap<>();

    static {
        funtionMap.put(int.class, INTEGER());
        funtionMap.put(Integer.class, INTEGER());
        funtionMap.put(long.class, LONG());
        funtionMap.put(Long.class, LONG());
        funtionMap.put(double.class, DOUBLE());
        funtionMap.put(Double.class, DOUBLE());
        funtionMap.put(String.class, STRING());
    }

    @Override
    public abstract R apply(T value);

    public static Function<String, Integer> INTEGER() {
        return value -> Integer.parseInt(value.trim());
    }

    public static Function<String, Long> LONG() {
        return value -> Long.parseLong(value.trim());
    }

    public static Function<String, Double> DOUBLE() {
        return value -> Double.parseDouble(value.trim());
    }

    private static Function<String, String> STRING() {
        return value -> value.trim();
    }

    public static <T> Function<String, T> of(Class<T> type) {
        return (Function<String, T>) funtionMap.get(type);
    }
}
