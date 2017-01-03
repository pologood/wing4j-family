package org.wing4j.orm.entity.utils;

import lombok.Data;
import org.wing4j.common.utils.StringUtils;
import org.wing4j.orm.*;
import org.wing4j.orm.entity.metadata.ColumnMetadata;
import org.wing4j.orm.entity.metadata.TableMetadata;
import org.wing4j.orm.mysql.DataEngine;
import org.wing4j.orm.mysql.DataEngineType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wing4j on 2017/1/2.
 * 实体生成工具
 */
public abstract class EntityUtils {
    static Set<String> STRING_SET = new HashSet<>();
    static Set<String> NUMBER_SET = new HashSet<>();
    static Set<String> DATE_SET = new HashSet<>();

    static {
        STRING_SET.add("VARCHAR");
        STRING_SET.add("TEXT");
        STRING_SET.add("CHAR");

        NUMBER_SET.add("NUMERIC");
        NUMBER_SET.add("DECIMAL");

        DATE_SET.add("DATE");
        DATE_SET.add("DATETIME");
        DATE_SET.add("TIMESTAMP");
    }

    /**
     * 根据表元信息列表生成实体
     * @param tableMetadatas 元信息列表
     * @param packageName   包名
     * @throws IOException 异常
     */
    public static void generate(List<TableMetadata> tableMetadatas, String packageName) throws IOException{
        String parnetDir = packageName.replaceAll("\\.", "/");
        File file = new File(parnetDir);
        if(!file.exists()){
            file.mkdirs();
        }
        for (TableMetadata tableMetadata : tableMetadatas){
            FileOutputStream fos = new FileOutputStream( parnetDir + "/" +  tableMetadata.getClassName() + ".java");
            generate(fos, tableMetadata, packageName);
            fos.close();
        }
    }
    /**
     * 根据元信息生成实体
     *
     * @param os            输入流
     * @param tableMetadata 元信息
     * @param packageName   包名
     * @throws IOException 异常
     */
    public static void generate(OutputStream os, TableMetadata tableMetadata, String packageName) throws IOException {
        for (String column : tableMetadata.getOrderColumns()) {
            ColumnMetadata columnMetadata = tableMetadata.getColumnMetadatas().get(column);
            convertField(columnMetadata);
        }
        StringBuilder head = new StringBuilder();
        head.append("package").append(" ").append(packageName).append(";\n");
        head.append("\n");
        head.append("import").append(" ").append(Data.class.getName()).append(";\n");
        head.append("import").append(" ").append("org.wing4j.orm.*").append(";\n");
        head.append("import").append(" ").append("org.wing4j.orm.mysql.*").append(";\n");
        head.append("import").append(" ").append(BigDecimal.class.getName()).append(";\n");
        head.append("import").append(" ").append(Date.class.getName()).append(";\n");
        head.append("\n");
        head.append("@").append(Data.class.getSimpleName()).append("\n");
        head.append("@").append(Table.class.getSimpleName()).append("(name = ").append("\"").append(tableMetadata.getTableName()).append("\"").append(", ").append("schema = ").append("\"").append(tableMetadata.getSchema()).append("\"").append(")\n");
        head.append("@").append(Comment.class.getSimpleName()).append("(\"").append(tableMetadata.getComment()).append("\"").append(")\n");
        if (tableMetadata.getDataEngine() != null) {
            head.append("@").append(DataEngine.class.getSimpleName()).append("(").append(DataEngineType.class.getSimpleName()).append(".").append(tableMetadata.getDataEngine()).append(")\n");
        }
        head.append("public").append(" ").append("class").append(" ").append(tableMetadata.getClassName()).append(" ").append("{\n");
        os.write(head.toString().getBytes());
        for (String column : tableMetadata.getOrderColumns()) {
            ColumnMetadata columnMetadata = tableMetadata.getColumnMetadatas().get(column);
            StringBuilder field = new StringBuilder();
            field.append("\t").append(generatePrimaryKeyAnnotation(columnMetadata)).append("\n");
            field.append("\t").append(generateColumnAnnotation(columnMetadata)).append("\n");
            field.append("\t").append(generateCommentAnnotation(columnMetadata)).append("\n");
            field.append("\t").append(columnMetadata.getJavaType().getSimpleName()).append(" ").append(columnMetadata.getJavaName()).append(";\n");
            os.write(field.toString().getBytes());
        }
        StringBuilder tail = new StringBuilder();
        tail.append("\n");
        tail.append("}\n");
        os.write(tail.toString().getBytes());
    }

    public static String generatePrimaryKeyAnnotation(ColumnMetadata columnMetadata) {
        if (columnMetadata.getTableMetadata().getPrimaryKeys().contains(columnMetadata.getJdbcName())) {
            StringBuilder sql = new StringBuilder("@");
            sql.append(PrimaryKey.class.getSimpleName());
            if (columnMetadata.getAutoIncrement()) {
                sql.append("(");
                sql.append("strategy = ");
                sql.append(PrimaryKeyStrategy.class.getSimpleName()).append(".").append("IDENTITY");
                sql.append(")");
            }
            return sql.toString();
        } else {
            return "";
        }
    }

    public static String generateCommentAnnotation(ColumnMetadata columnMetadata) {
        StringBuilder sql = new StringBuilder("@");
        sql.append(Comment.class.getSimpleName()).append("(\"");
        sql.append(columnMetadata.getComment());
        sql.append("\")");
        return sql.toString();
    }

    /**
     * 生成字段注解
     *
     * @param columnMetadata
     * @return
     */
    public static String generateColumnAnnotation(ColumnMetadata columnMetadata) {
        StringBuilder sql = new StringBuilder("@");
        if (columnMetadata.getDataType().startsWith("VARCHAR")) {
            String str_len = columnMetadata.getDataType().substring("VARCHAR".length() + 1, columnMetadata.getDataType().length() - 1);
            sql.append(StringColumn.class.getSimpleName()).append("(");
            sql.append("name = ").append("\"");
            sql.append(columnMetadata.getJdbcName());
            sql.append("\" ");
            sql.append(",length = ");
            sql.append(Integer.valueOf(str_len));
            sql.append(" ");
            sql.append(",type = ");
            sql.append(StringType.class.getSimpleName()).append(".").append("VARCHAR");
            sql.append(" ");
            sql.append(",nullable = ");
            sql.append(columnMetadata.getNullable());
            sql.append(" ");
            if (columnMetadata.getDefaultValue() != null &&!columnMetadata.getDefaultValue().isEmpty()) {
                sql.append(",defaultValue = ").append("\"");
                sql.append(columnMetadata.getDefaultValue());
                sql.append("\" ");
            }
            sql.append(")");
        } else if (columnMetadata.getDataType().startsWith("CHAR")) {
            String str_len = columnMetadata.getDataType().substring("CHAR".length() + 1, columnMetadata.getDataType().length() - 1);
            sql.append(StringColumn.class.getSimpleName()).append("(");
            sql.append("name = ").append("\"");
            sql.append(columnMetadata.getJdbcName());
            sql.append("\" ");
            sql.append(",length = ");
            sql.append(Integer.valueOf(str_len));
            sql.append(" ");
            sql.append(",type = ");
            sql.append(StringType.class.getSimpleName()).append(".").append("CHAR");
            sql.append(" ");
            sql.append(",nullable = ");
            sql.append(columnMetadata.getNullable());
            sql.append(" ");
            if (columnMetadata.getDefaultValue() != null &&!columnMetadata.getDefaultValue().isEmpty()) {
                sql.append(",defaultValue = ").append("\"");
                sql.append(columnMetadata.getDefaultValue());
                sql.append("\" ");
            }
            sql.append(")");
        } else if (columnMetadata.getDataType().equals("TEXT")) {
            sql.append(StringColumn.class.getSimpleName()).append("(");
            sql.append("name = ").append("\"");
            sql.append(columnMetadata.getJdbcName());
            sql.append("\" ");
            sql.append(" ");
            sql.append(",type = ");
            sql.append(StringType.class.getSimpleName()).append(".").append("TEXT");
            sql.append(" ");
            sql.append(",nullable = ");
            sql.append(columnMetadata.getNullable());
            sql.append(" ");
            if (columnMetadata.getDefaultValue() != null &&!columnMetadata.getDefaultValue().isEmpty()) {
                sql.append(",defaultValue = ").append("\"");
                sql.append(columnMetadata.getDefaultValue());
                sql.append("\" ");
            }
            sql.append(")");
        } else if (columnMetadata.getJdbcType().equals("NUMERIC")) {
            sql.append(NumberColumn.class.getSimpleName()).append("(");
            sql.append("name = ").append("\"");
            sql.append(columnMetadata.getJdbcName());
            sql.append("\" ");
            sql.append(",type = ");
            sql.append(NumberType.class.getSimpleName()).append(".").append("INTEGER");
            sql.append(" ");
            sql.append(",nullable = ");
            sql.append(columnMetadata.getNullable());
            sql.append(" ");
            if (columnMetadata.getDefaultValue() != null &&!columnMetadata.getDefaultValue().isEmpty()) {
                sql.append(",defaultValue = ").append("\"");
                sql.append(columnMetadata.getDefaultValue());
                sql.append("\" ");
            }
            sql.append(")");
        } else if (columnMetadata.getJdbcType().equals("DECIMAL")) {
            String str_scale = columnMetadata.getDataType().substring("DECIMAL".length() + 1, columnMetadata.getDataType().lastIndexOf(","));
            String str_precision = columnMetadata.getDataType().substring(columnMetadata.getDataType().lastIndexOf(",") + 1, columnMetadata.getDataType().length() - 1);
            sql.append(NumberColumn.class.getSimpleName()).append("(");
            sql.append("name = ").append("\"");
            sql.append(columnMetadata.getJdbcName());
            sql.append("\" ");
            sql.append(",scale = ");
            sql.append(Integer.valueOf(str_scale));
            sql.append(" ");
            sql.append(",precision = ");
            sql.append(Integer.valueOf(str_precision));
            sql.append(" ");
            sql.append(",type = ");
            sql.append(NumberType.class.getSimpleName()).append(".").append("DECIMAL");
            sql.append(" ");
            sql.append(",nullable = ");
            sql.append(columnMetadata.getNullable());
            sql.append(" ");
            if (columnMetadata.getDefaultValue() != null &&!columnMetadata.getDefaultValue().isEmpty()) {
                sql.append(",defaultValue = ").append("\"");
                sql.append(columnMetadata.getDefaultValue());
                sql.append("\" ");
            }
            sql.append(")");
        } else if (columnMetadata.getJdbcType().equals("TIMESTAMP")) {
            sql.append(DateColumn.class.getSimpleName()).append("(");
            sql.append("name = ").append("\"");
            sql.append(columnMetadata.getJdbcName());
            sql.append("\" ");
            sql.append(",type = ");
            sql.append(DateType.class.getSimpleName()).append(".").append("TIMESTAMP");
            sql.append(" ");
            sql.append(",nullable = ");
            sql.append(columnMetadata.getNullable());
            sql.append(" ");
            if (columnMetadata.getDefaultValue() != null &&!columnMetadata.getDefaultValue().isEmpty()) {
                sql.append(",defaultValue = ").append("\"");
                sql.append(columnMetadata.getDefaultValue());
                sql.append("\" ");
            }
            sql.append(")");
        } else if (columnMetadata.getJdbcType().equals("DATE")) {
            sql.append(DateColumn.class.getSimpleName()).append("(");
            sql.append("name = ").append("\"");
            sql.append(columnMetadata.getJdbcName());
            sql.append("\" ");
            sql.append(",type = ");
            sql.append(DateType.class.getSimpleName()).append(".").append("DATE");
            sql.append(" ");
            sql.append(",nullable = ");
            sql.append(columnMetadata.getNullable());
            sql.append(" ");
            if (columnMetadata.getDefaultValue() != null &&!columnMetadata.getDefaultValue().isEmpty()) {
                sql.append(",defaultValue = ").append("\"");
                sql.append(columnMetadata.getDefaultValue());
                sql.append("\" ");
            }
            sql.append(")");
        } else if (columnMetadata.getJdbcType().equals("DATETIME")) {
            sql.append(DateColumn.class.getSimpleName()).append("(");
            sql.append("name = ").append("\"");
            sql.append(columnMetadata.getJdbcName());
            sql.append("\" ");
            sql.append(",type = ");
            sql.append(DateType.class.getSimpleName()).append(".").append("TIME");
            sql.append(" ");
            sql.append(",nullable = ");
            sql.append(columnMetadata.getNullable());
            sql.append(" ");
            if (columnMetadata.getDefaultValue() != null &&!columnMetadata.getDefaultValue().isEmpty()) {
                sql.append(",defaultValue = ").append("\"");
                sql.append(columnMetadata.getDefaultValue());
                sql.append("\" ");
            }
            sql.append(")");
        }
        return sql.toString();
    }

    public static void convertField(ColumnMetadata columnMetadata) {
        if (STRING_SET.contains(columnMetadata.getJdbcType())) {
            columnMetadata.setJavaType(String.class);
        } else if (NUMBER_SET.contains(columnMetadata.getJdbcType())) {
            if ("NUMERIC".equalsIgnoreCase(columnMetadata.getJdbcType())) {
                columnMetadata.setJavaType(Integer.class);
            } else if ("DECIMAL".equalsIgnoreCase(columnMetadata.getJdbcType())) {
                columnMetadata.setJavaType(BigDecimal.class);
            }
        } else if (DATE_SET.contains(columnMetadata.getJdbcType())) {
            columnMetadata.setJavaType(Date.class);
        } else {
            columnMetadata.setJavaType(String.class);
        }
        columnMetadata.setJavaName(StringUtils.underlineToCamel(columnMetadata.getJdbcName()));
    }
}
