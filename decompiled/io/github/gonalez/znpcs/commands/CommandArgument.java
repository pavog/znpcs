package io.github.gonalez.znpcs.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandArgument {
   String[] value() default {};
}
