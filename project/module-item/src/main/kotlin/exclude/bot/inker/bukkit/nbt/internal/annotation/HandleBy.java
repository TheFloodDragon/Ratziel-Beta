package exclude.bot.inker.bukkit.nbt.internal.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(HandleBy.List.class)
public @interface HandleBy {
  CbVersion version();

  String reference();

  boolean isInterface() default false;

  boolean accessor() default false;


  @Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {
    HandleBy[] value();
  }
}
