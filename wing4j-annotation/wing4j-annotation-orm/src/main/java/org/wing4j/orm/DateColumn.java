package org.wing4j.orm;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wing4j on 2016/12/17.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateColumn {
    /**
     * 字段名称
     *
     * @return 字段名称
     */
    String name() default "";

    /**
     * 是否允许为空
     *
     * @return 是否允许为空
     */
    boolean nullable() default true;
    /**
     * 默认值
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * 字段数据类型
     *
     * @return
     */
    DateType type() default DateType.TIMESTAMP;
}
