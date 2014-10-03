package ch.swissbytes.fqmes.report;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by christian on 13/06/14.
 */
@Qualifier
@Retention(RUNTIME)
@Target( { TYPE, METHOD, FIELD, PARAMETER } )
public @interface ProviderTypeForReport {
}
