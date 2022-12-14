package examples.common.telegram.encode;

import examples.common.lang.Pad;
import examples.common.lang.exception.InitializeEception;
import cj.tlj.app.common.telegram.annotation.*;
import examples.common.telegram.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EncodeItemHandlers {
    private static Logger logger = LoggerFactory.getLogger(EncodeItemHandlers.class);
    private static final Map<Class, EncodeItemHandler> handlerMap = new HashMap<>();

    public static <T> EncodeItemHandler<T> of(T targetObject) {
        Class<T> targetClass = (Class<T>) targetObject.getClass();
        EncodeItemHandler<T> handler = handlerMap.get(targetClass);
        if(handler == null){
            handler = compositeItem(targetClass);
            handlerMap.put(targetClass, handler);
        }

        return handler;
    }

    public static <T, U> EncodeItemHandler<T> fieldItem(Class<T> domainClass, Class<U> fieldClass, Field field, FieldItem itemAnnotation) {

        Function<T, String> getter = fieldGetter(domainClass, fieldClass, field)
                .andThen(valueVerifier(fieldClass, itemAnnotation.size()))
                .andThen(itemAnnotation.pad().getFunction(itemAnnotation.size()))
                .andThen(value -> {
                    logger.debug("[{}] : [{}]", field.getName(), value);
                    return value;
                });
        return (source, value) -> source.append(getter.apply(value));
    }

    public static <T> EncodeItemHandler<T> fillerItem(Class<T> targetClass, int size) {
        Function<String, String> filler = Pad.LEFT_SPACE.getFunction(size);
        return (source, value) -> source.append(filler.apply(""));
    }

    public static <T> EncodeItemHandler<T> compositeItem(Class<T> targetClass) {

        Protocol trAnnotation = targetClass.getDeclaredAnnotation(Protocol.class);
        EncodeItemHandler<T>[] itemHandlers = new EncodeItemHandler[trAnnotation.itemCount()];
        Field[] fields = targetClass.getDeclaredFields();

        logger.debug("-------------------------------------------------------------------------------");
        Arrays.stream(fields).forEach(field -> {
            Class<?> fieldClass = field.getType();

            if (field.isAnnotationPresent(FieldItem.class)) {
                FieldItem itemAnnotation = field.getDeclaredAnnotation(FieldItem.class);
                logger.debug("[{}] {} = {} : {} : {}", itemAnnotation.seq(), field.getName(), fieldClass.getSimpleName(), itemAnnotation.size(), itemAnnotation.pad());
                itemHandlers[itemAnnotation.seq()] = fieldItem(targetClass, fieldClass, field, itemAnnotation);

            } else if (field.isAnnotationPresent(CompositeItem.class)) {
                CompositeItem itemAnnotation = field.getDeclaredAnnotation(CompositeItem.class);
                logger.debug("[{}] {} = {}", itemAnnotation.seq(), field.getName(), fieldClass.getSimpleName());
                itemHandlers[itemAnnotation.seq()] = compositeItemDelegator(targetClass, fieldClass, field);

            } else if (field.isAnnotationPresent(ListItem.class)) {
                ListItem itemAnnotation = field.getDeclaredAnnotation(ListItem.class);
                logger.debug("[{}] {}_cnt = counter : {}", itemAnnotation.countSeq(), field.getName(), itemAnnotation.countSize());
                itemHandlers[itemAnnotation.countSeq()] = listItemCount(targetClass, (Class<List<T>>) fieldClass, field, itemAnnotation);
                logger.debug("[{}] {} = {}", itemAnnotation.dataSeq(), field.getName(), fieldClass.getSimpleName());
                itemHandlers[itemAnnotation.dataSeq()] = listItem(targetClass, (Class<List<T>>) fieldClass, field, itemAnnotation);
            }
        });

        Filler[] fillers = trAnnotation.fillers();
        Arrays.stream(fillers).forEach(filler -> {
            itemHandlers[filler.seq()] = fillerItem(targetClass, filler.size());
        });
        logger.debug("-------------------------------------------------------------------------------");

        return (source, value) -> {
            for(EncodeItemHandler handler : itemHandlers) handler.handle(source, value);
        } ;
    }

    public static <P, T> EncodeItemHandler<P> compositeItemDelegator(Class<P> parentClass, Class<T> targetClass, Field field){

        EncodeItemHandler<T> handler = compositeItem(targetClass);
        Function<P, T> getter = fieldGetter(parentClass, targetClass, field);

        return (source, value) -> handler.handle(source, getter.apply(value));
    }

    public static <P, T> EncodeItemHandler<P> listItemCount(Class<P> parentClass, Class<List<T>> targetClass, Field field, ListItem itemAnnotation){
        if(!targetClass.isAssignableFrom(List.class))
            throw new InitializeEception("@ListItem??? List??? ?????????????????? - "+field.getName()+"|"+targetClass.getName());

        return new EncodeItemHandler<P>() {
            Function<P, String> getter = fieldGetter(parentClass, targetClass, field)
                    .andThen((value) -> String.valueOf(value.size()) )
                    .andThen(itemAnnotation.countPad().getFunction(itemAnnotation.countSize()));
            @Override
            public void handle(TelegramRequestSource source, P value) {
                if(value == null) throw new NullPointerException(field.getName());
                source.append(getter.apply(value));
            }
        };
    }

    public static <P, T> EncodeItemHandler<P> listItem(Class<P> parentClass, Class<List<T>> targetClass, Field field, ListItem itemAnnotation){
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        Class<T> listClass = (Class<T>) listType.getActualTypeArguments()[0];

        return new EncodeItemHandler<P>() {
            EncodeItemHandler<T> handler = compositeItem(listClass);
            Function<P, List<T>> getter = fieldGetter(parentClass, targetClass, field);
            @Override
            public void handle(TelegramRequestSource source, P value) {
                if(value == null) throw new NullPointerException(field.getName());
                getter.apply(value).forEach(item -> handler.handle(source, item));

            }
        };
    }

    private static <T, R> Function<T,R> fieldGetter(Class<T> parentClazz, Class<R> fieldClazz, Field field) {
        field.setAccessible(true);
        return object -> {
            try {
                return (R) field.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static <T> Function<T, String> valueVerifier(Class<T> fieldClazz, int size) {
        return value -> {
            if (value == null) return "";

            String valueString = String.valueOf(value);

            if(valueString.length() > size){
                String returnValue = valueString.substring(0, size);
                logger.debug("Too long value[{}] return [{}]", valueString, returnValue);
                return returnValue;
            }
            return valueString;
        };
    }
}