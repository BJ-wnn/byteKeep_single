package org.wnn.bytekeep.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author NanNan Wang
 */
@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "app.cache")
public class CacheConfig {

    private Map<String, CacheSpec> configs = new HashMap<>();

    public Map<String, CacheSpec> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, CacheSpec> configs) {
        this.configs = configs;
    }

    @Data
    public static class CacheSpec {
        private String expire;
        private Integer maxSize;
    }

    @Bean
    public CacheManager cacheManager() {
      return new CaffeineCacheManager() {
          @Override
          protected Cache createNativeCaffeineCache(String name) {
              // 提取前缀，如 token:user => token
              String prefix = name.contains(":") ? name.split(":")[0] : "default";

              // 获取配置，找不到则用 default 配置
              CacheSpec spec = configs.getOrDefault(prefix, configs.get("default"));

              if (spec == null) {
                  throw new IllegalArgumentException("No cache config found for prefix: " + prefix);
              }

              // 解析过期时间，如果为空则使用默认值
              Duration duration = parseDuration(spec.getExpire());

              // 设置最大容量，默认 500
              int maxSize = Optional.ofNullable(spec.getMaxSize()).orElse(500);

              // 构建并返回 Caffeine 缓存实例
              return Caffeine.newBuilder()
                      .expireAfterWrite(duration)
                      .maximumSize(maxSize)
                      .build();
          }
      };
    }

    private Duration parseDuration(String s) {
        if (s.endsWith("s")) return Duration.ofSeconds(Long.parseLong(s.replace("s", "")));
        if (s.endsWith("m")) return Duration.ofMinutes(Long.parseLong(s.replace("m", "")));
        if (s.endsWith("h")) return Duration.ofHours(Long.parseLong(s.replace("h", "")));
        throw new IllegalArgumentException("Invalid duration: " + s);
    }

}
