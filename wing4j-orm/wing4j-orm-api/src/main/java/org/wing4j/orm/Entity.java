package org.wing4j.orm;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体基类
 */
@Data
@ToString
public abstract class Entity {
    /**
     * 数据库模式
     */
    @org.wing4j.orm.Ignore
    protected String schema;
    /**
     * 排序字段
     */
    protected final List<String> orderBys = new ArrayList<>();
}
