package org.wnn.bytekeep.config.dal;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author NanNan Wang
 */
@Configuration
@MapperScan(basePackages = {"org.wnn.bytekeep.demo.dao.db1"},sqlSessionTemplateRef = "db1SqlSessionTemplate")
public class Db1Config {

    @Primary
    @Bean(name = "db1DataSource")
    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.db1")
    public DataSource dataSource() {
        return new AtomikosDataSourceBean();
    }

    @Primary
    @Bean(name = "db1SqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("db1DataSource") DataSource dataSource,
                                               @Qualifier("globalMyBatisInterceptors") Interceptor[] interceptors) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/**/*.xml"));
        factoryBean.setPlugins(interceptors);
        return factoryBean.getObject();
    }


    @Primary
    @Bean(name = "db1SqlSessionTemplate")
    public SqlSessionTemplate db1SqlSessionTemplate(@Qualifier("db1SqlSessionFactory") SqlSessionFactory sf) {
        return new SqlSessionTemplate(sf);
    }


    @Bean(name = "db1TransactionTemplate")
    public TransactionTemplate db1TransactionTemplate(@Qualifier("db1DataSource") DataSource dataSource) {
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        return new TransactionTemplate(transactionManager);
    }

}
