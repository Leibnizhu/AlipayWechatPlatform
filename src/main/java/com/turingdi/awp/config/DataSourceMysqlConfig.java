package com.turingdi.awp.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据源配置、初始化数据库连接池
 */
@Configuration
@MapperScan(basePackages = "com.turingdi.awp.dao", sqlSessionTemplateRef = "mysqlSqlSessionTemplate")
public class DataSourceMysqlConfig {

    private Logger logger = LoggerFactory.getLogger(DataSourceMysqlConfig.class);

    @Value("${spring.datasource.mysql.url}")
    private String dbUrl;

    @Value("${spring.datasource.mysql.username}")
    private String username;

    @Value("${spring.datasource.mysql.password}")
    private String password;

    @Value("${spring.datasource.mysql.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.mysql.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.mysql.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.mysql.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.mysql.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.mysql.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.mysql.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.mysql.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.mysql.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.mysql.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.mysql.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.mysql.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.mysql.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.mysql.filters}")
    private String filters;

    //    @Value("{spring.datasource.connectionProperties}")
//    private String connectionProperties;
    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource testDataSource() {
        DruidDataSource datasource = new DruidDataSource();

        datasource.setUrl(this.dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        //configuration
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            datasource.setFilters(filters);
        } catch (SQLException e) {
        }
//        datasource.setConnectionProperties(connectionProperties);
        return datasource;//DataSourceBuilder.create().build();
    }

    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        pageHelper.setProperties(p);
        // 添加插件
        bean.setPlugins(new Interceptor[]{pageHelper});
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "mysqlTransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "mysqlSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("mysqlSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}