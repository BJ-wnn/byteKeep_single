package org.wnn.bytekeep.config.dal;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * @author NanNan Wang
 */
@Configuration
public class JtaTransactionManagerConfig {

    // UserTransactionImp 是 Atomikos 的事务接口的实现类，负责具体的事务操作。
    @Bean
    public UserTransactionImp userTransaction() throws Throwable {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(300); // 设置事务超时时间
        return userTransactionImp;
    }

    //UserTransactionManager 是 Atomikos 提供的 JTA 事务管理器，它负责协调不同的事务资源。
    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() throws Throwable {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.init();
        return userTransactionManager;
    }


    //JtaTransactionManager 是 Spring 提供的 JTA 事务管理器，它需要传入 UserTransaction 和 TransactionManager（即 Atomikos 的实现）。
    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(TransactionManager transactionManager, UserTransaction userTransaction) {
        return new JtaTransactionManager(userTransaction, transactionManager);
    }

    // 编程式事务
    @Primary
    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

}
