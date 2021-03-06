package org.wing4j.orm.mybatis.mapper.builder.delete;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.transaction.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.wing4j.orm.Constants;
import org.wing4j.orm.WordMode;
import org.wing4j.orm.mybatis.mapper.builder.BaseTest;
import org.wing4j.orm.mybatis.mapper.builder.DemoCrudMapper;
import org.wing4j.orm.mybatis.mapper.builder.DemoEntity;
import org.wing4j.orm.mybatis.mapper.builder.MappedStatementBuilder;
import org.wing4j.orm.mybatis.mapper.builder.insert.InsertMappedStatementBuilder;
import org.wing4j.orm.mybatis.spring.transaction.SpringManagedTransaction;
import org.wing4j.test.CreateTable;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;

@ContextConfiguration(locations = {"classpath*:testContext-dev.xml"})
public class DeleteOrMappedStatementBuilderTest extends BaseTest {
    @Autowired
    DataSource dataSource;

    @CreateTable(entities = DemoEntity.class, sqlMode = WordMode.upperCase, keywordMode = WordMode.upperCase)
    @Test
    public void testBuild() throws Exception {
        //创建配置文件
        final Configuration config = new Configuration();
        config.setCacheEnabled(true);
        config.setLazyLoadingEnabled(false);
        config.setAggressiveLazyLoading(true);
        Transaction transaction = new SpringManagedTransaction(dataSource);
        final Executor executor = config.newExecutor(transaction);
        SqlSession sqlSession = new DefaultSqlSession(config, executor, false);
        {
            MappedStatementBuilder builder = new InsertMappedStatementBuilder(config, DemoCrudMapper.class, WordMode.upperCase, WordMode.upperCase, false, null);
            MappedStatement ms = builder.build();
            config.addMappedStatement(ms);
            {
                DemoEntity demoEntity = new DemoEntity();
                demoEntity.setSerialNo(UUID.randomUUID().toString());
                demoEntity.setCol1("col1_test");
                demoEntity.setCol2(new BigDecimal("1"));
                demoEntity.setCol3(1);
                int cnt = sqlSession.insert(Constants.INSERT, demoEntity);
                Assert.assertEquals(1, cnt);
            }
            {
                DemoEntity demoEntity = new DemoEntity();
                demoEntity.setSerialNo(UUID.randomUUID().toString());
                demoEntity.setCol1("col2_test");
                demoEntity.setCol2(new BigDecimal("2"));
                demoEntity.setCol3(2);
                int cnt = sqlSession.insert(Constants.INSERT, demoEntity);
                Assert.assertEquals(1, cnt);
            }
        }
        MappedStatementBuilder builder = new DeleteOrMappedStatementBuilder(config, DemoCrudMapper.class, WordMode.upperCase, WordMode.upperCase, false);
        MappedStatement ms = builder.build();
        config.addMappedStatement(ms);
        {
            DemoEntity demoEntity = new DemoEntity();
            demoEntity.setCol1("col3_test");
            demoEntity.setCol2(new BigDecimal("3"));
            int cnt = sqlSession.delete(Constants.DELETE_OR, demoEntity);
            Assert.assertEquals(0, cnt);
        }
        {
            DemoEntity demoEntity = new DemoEntity();
            demoEntity.setCol1("col1_test");
            demoEntity.setCol2(new BigDecimal("2"));
            demoEntity.setCol3(12);
            int cnt = sqlSession.delete(Constants.DELETE_OR, demoEntity);
            Assert.assertEquals(2, cnt);
        }
    }
}