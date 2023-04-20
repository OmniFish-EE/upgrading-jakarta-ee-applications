package javax.faces.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Only required for OmniFaces older than 4.x
 */
@Retention(value=RetentionPolicy.RUNTIME)
 @Target(value=ElementType.TYPE)
 @Inherited
 @Deprecated
public @interface ApplicationScoped {
    
}
