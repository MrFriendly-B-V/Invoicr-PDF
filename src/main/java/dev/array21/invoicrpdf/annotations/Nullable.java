package dev.array21.invoicrpdf.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes that the annotated object might be null<br>
 * You should probably perform a null-check on it.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER, ElementType.TYPE_PARAMETER})
public @interface Nullable {}
