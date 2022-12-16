package pe.devgon.functional.lang;

import java.util.function.Function;

public enum TypeCast implements Function{
    STRING(){
        public String apply(Object value) {
            return String.valueOf(value);
        }
    },
    INT(){
        public Integer apply(Object value) {
            if (value instanceof String)
                return Integer.valueOf((String) value);
            else
                return ((Number)value).intValue();
        }
    },
    INTEGER(){
        public Integer apply(Object value) {
            if (value instanceof String)
                return Integer.valueOf((String) value);
            else
                return ((Number)value).intValue();
        }
    };


    public static TypeCast of(Class<?> typeClass) {
        TypeCast typeCast = TypeCast.valueOf(typeClass.getSimpleName().toUpperCase());

        return typeCast;
    }
}
