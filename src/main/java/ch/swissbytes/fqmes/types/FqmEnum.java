package ch.swissbytes.fqmes.types;

/**
 * Created by christian on 16/09/14.
 */
public interface FqmEnum<T> {

    boolean equalsTo(final T t);

    boolean equalsTo(final Integer id);



}
