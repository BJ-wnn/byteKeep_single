package org.wnn.bytekeep.core.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author NanNan Wang
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
@Component
@ConditionalOnProperty(prefix = "slow.sql", name = "threshold", havingValue = "true")
public class SlowSqlInterceptor implements Interceptor {


    private static final Logger logger = LoggerFactory.getLogger("slowSqlLog");


    @Value("${slow.sql.threshold}")
    // 慢 SQL 阈值（毫秒）
    private long slowSqlThreshold;

    @Value("${slow.sql.service-name:unknown-service}")  // 注入配置，设置默认值
    private String serviceName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
//        // 获取 SQL 语句
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql().replaceAll("\\s+", " ").trim();

        Object parameterObject = statementHandler.getBoundSql().getParameterObject();
        String parameters = parameterObject != null ? parameterObject.toString() : "No parameters";

        // 计算 SQL 执行时间（毫秒）
        long start = System.nanoTime();
        // SQL执行
        Object result = invocation.proceed();
        long end = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(end - start);

        // 如果 SQL 执行时间超过阈值，记录慢 SQL
        if (durationMs > slowSqlThreshold) {
            // 构建SQL详细信息（嵌套对象）
            Map<String, Object> sqlInfo = new LinkedHashMap<>();
            sqlInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            sqlInfo.put("durationMs", durationMs);
            sqlInfo.put("sql", sql);
            sqlInfo.put("parameters", parameters);
            sqlInfo.put("threshold", slowSqlThreshold);

            // 构建顶级日志对象
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("service_name", serviceName);
            logData.put("sql_info", sqlInfo); // SQL信息作为嵌套字段

            // 直接将traceId作为顶级字段
            String traceId = MDC.get("traceId");
            if (traceId != null) {
                logData.put("traceId", traceId);
            }

            try {
                // 输出JSON格式日志
                String jsonLog = objectMapper.writeValueAsString(logData);
                logger.info(jsonLog);
            } catch (JsonProcessingException e) {
                // 序列化失败时回退到普通日志
                logger.info("Slow SQL (JSON serialization failed): duration={}ms, sql={}, parameters={}",
                        durationMs, sql, parameters);
            }
        }

        return result;
    }
}
