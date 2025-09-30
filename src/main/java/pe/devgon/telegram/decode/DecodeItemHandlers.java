package pe.devgon.telegram.decode;

import lombok.extern.slf4j.Slf4j;
import pe.devgon.functional.lang.CastTo;
import pe.devgon.telegram.CountHolder;
import pe.devgon.telegram.annotation.CompositeItem;
import pe.devgon.telegram.annotation.FieldItem;
import pe.devgon.telegram.annotation.Filler;
import pe.devgon.telegram.annotation.ListItem;
import pe.devgon.telegram.annotation.Protocol;
import pe.devgon.telegram.exception.InitializeEception;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class DecodeItemHandlers {
    private static final Map<Class, DecodeItemHandler> handlerMap = new HashMap<>();

    public static <T> DecodeItemHandler<T> of(Class<T> targetClass) {
        DecodeItemHandler<T> handler = handlerMap.get(targetClass);
        if (handler == null) {
            handler = compositeItem(targetClass);
            handlerMap.put(targetClass, handler);
        }
        return handler;
    }

    public static <T, U> DecodeItemHandler<T> fieldItem(Class<T> domainClass, Class<U> fieldClass, Field field, FieldItem itemAnnotation) {
        Function<TelegramResponseSource, U> getter = responseSourceGetter(itemAnnotation.size())
                .andThen(CastTo.of(fieldClass));
        field.setAccessible(true);

        return (object, source) -> {
            try {
                U value = getter.apply(source);
                field.set(object, value);
                log.debug("[{}] : [{}]", field.getName(), value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T> DecodeItemHandler<T> fillerItem(Class<T> domainClass, int size) {
        return (object, source) -> source.read(size);
    }

    public static <T> DecodeItemHandler<T> compositeItem(Class<T> targetClass) {
        Protocol reqAnnotation = targetClass.getAnnotation(Protocol.class);
        DecodeItemHandler<T>[] itemHandlers = new DecodeItemHandler[reqAnnotation.itemCount()];
        Field[] fields = targetClass.getDeclaredFields();

        log.debug("-------------------------------------------------------------------------------");
        Arrays.stream(fields).forEach(field -> {
            Class<?> fieldClass = field.getType();

            if (field.isAnnotationPresent(FieldItem.class)) {
                FieldItem itemAnnotation = field.getDeclaredAnnotation(FieldItem.class);
                log.debug("[{}] {} = {} : {}", itemAnnotation.seq(), field.getName(), fieldClass.getSimpleName(), itemAnnotation.size());
                itemHandlers[itemAnnotation.seq()] = fieldItem(targetClass, fieldClass, field, itemAnnotation);

            } else if (field.isAnnotationPresent(CompositeItem.class)) {
                CompositeItem itemAnnotation = field.getDeclaredAnnotation(CompositeItem.class);
                log.debug("[{}] {} = {}", itemAnnotation.seq(), field.getName(), fieldClass.getSimpleName());
                itemHandlers[itemAnnotation.seq()] = compositeItemDelegator(targetClass, fieldClass, field);

            } else if (field.isAnnotationPresent(ListItem.class)) {
                ListItem itemAnnotation = field.getDeclaredAnnotation(ListItem.class);
                CountHolder countHolder = new CountHolder();
                log.debug("[{}] {}_cnt = counter : {}", itemAnnotation.countSeq(), field.getName(), itemAnnotation.countSize());
                itemHandlers[itemAnnotation.countSeq()] = listItemCount(targetClass, (Class<List<T>>) fieldClass, field, itemAnnotation, countHolder);
                log.debug("[{}] {} = {}", itemAnnotation.dataSeq(), field.getName(), fieldClass.getSimpleName());
                itemHandlers[itemAnnotation.dataSeq()] = listItem(targetClass, (Class<List<T>>) fieldClass, field, itemAnnotation, countHolder);
            }
        });

        Filler[] fillers = reqAnnotation.fillers();
        Arrays.stream(fillers).forEach(filler -> {
            log.debug("[{}] filler = filler : {}", filler.seq(), filler.size());
            itemHandlers[filler.seq()] = fillerItem(targetClass, filler.size());
        });

        log.debug("-------------------------------------------------------------------------------");

        return (object, source) -> {
            for (DecodeItemHandler handler : itemHandlers) {
                try {
                    handler.handle(object, source);
                } catch (Exception e) {
                    log.debug("Error occur during decode - {}", e.getMessage());
                }
            }
        };
    }

    public static <P, T> DecodeItemHandler<P> compositeItemDelegator(Class<P> parentClass, Class<T> targetClass, Field field) {
        DecodeItemHandler<T> handler = compositeItem(targetClass);
        field.setAccessible(true);

        return (parent, source) -> {
            try {
                T target = targetClass.getDeclaredConstructor().newInstance();
                field.set(parent, target);
                handler.handle(target, source);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <P, T> DecodeItemHandler<P> listItemCount(Class<P> parentClass, Class<List<T>> targetClass, Field field, ListItem itemAnnotation, CountHolder holder) {
        if (!List.class.isAssignableFrom(targetClass)) {
            throw new InitializeEception("@ListItem 由ъ뒪????낆씠 ?꾨떂 - " + field.getName() + "|" + targetClass.getName());
        }

        long fixedCount = itemAnnotation.fixedCount();

        if (fixedCount > 0) {
            return (object, source) -> holder.set(itemAnnotation.fixedCount());
        } else {
            return (object, source) -> holder.set(Long.parseLong(source.read(itemAnnotation.countSize())));
        }
    }

    public static <P, T> DecodeItemHandler<P> listItem(Class<P> parentClass, Class<List<T>> targetClass, Field field, ListItem itemAnnotation, CountHolder holder) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        Class<T> listClass = (Class<T>) listType.getActualTypeArguments()[0];
        DecodeItemHandler<T> handler = compositeItem(listClass);
        field.setAccessible(true);

        return (object, source) -> {
            List<T> itemList = new ArrayList<>();
            try {
                field.set(object, itemList);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            for (int index = 0; index < holder.get(); index++) {
                try {
                    T target = listClass.getDeclaredConstructor().newInstance();
                    itemList.add(target);
                    handler.handle(target, source);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Function<TelegramResponseSource, String> responseSourceGetter(int size) {
        return source -> source.read(size);
    }
}


