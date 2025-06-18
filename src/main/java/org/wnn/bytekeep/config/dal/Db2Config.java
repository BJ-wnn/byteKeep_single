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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @author NanNan Wang
 */
@Configuration
@MapperScan(basePackages = {"org.wnn.bytekeep.demo.dao.db2"},sqlSessionTemplateRef = "db2SqlSessionTemplate")
public class Db2Config {

    @Bean(name = "db2DataSource")
    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.db2")
    public DataSource dataSource() {
        return new AtomikosDataSourceBean();
    }

    @Bean(name = "db2SqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("db2DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/**/*.xml"));
        return factoryBean.getObject();
    }

    @Bean(name = "db2SqlSessionTemplate")
    public SqlSessionTemplate db2SqlSessionTemplate(@Qualifier("db2SqlSessionFactory") SqlSessionFactory sf) {
        return new SqlSessionTemplate(sf);
    }


}
