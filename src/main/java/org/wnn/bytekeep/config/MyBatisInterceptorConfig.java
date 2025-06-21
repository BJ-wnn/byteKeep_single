package org.wnn.bytekeep.config;

import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wnn.bytekeep.core.log.SlowSqlInterceptor;

/**
 * @author NanNan Wang
 */
@Configuration
public class MyBatisInterceptorConfig {

    @Bean
    public Interceptor slowSqlInterceptor() {
        return new SlowSqlInterceptor();
    }

    @Bean
    public Interceptor[] globalMyBatisInterceptors(@Qualifier("slowSqlInterceptor") Interceptor slowSqlInterceptor) {
        return new Interceptor[]{slowSqlInterceptor};
    }
}
