package org.wnn.bytekeep.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * 通用缓存操作辅助类，封装 Spring Cache 的常用方法。
 * 兼容 Caffeine、Redis、EhCache 等 Spring 支持的缓存实现。
 * 支持缓存读取、写入、清除、懒加载、缓存存在判断等操作。
 *
 * 使用示例：
 * <pre>
 *     cacheHelper.put("myCache", "key1", "value1");
 *     String value = cacheHelper.get("myCache", "key1");
 *     boolean exists = cacheHelper.contains("myCache", "key1");
 * </pre>
 *
 * 注意：不同缓存中间件的 TTL、大小限制等配置应在 CacheManager 初始化阶段统一配置。
 *
 * @author NanNan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheHelper {

    private final CacheManager cacheManager;

    /**
     * 获取缓存对象（必须存在），否则抛出异常。
     *
     * @param cacheName 缓存名称
     * @return 缓存对象
     */
    private Cache getRequiredCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("未配置缓存: " + cacheName);
        }
        return cache;
    }

    /**
     * 从缓存获取值，如果不存在则调用 loader 加载，并自动写入缓存。
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param loader    加载函数，用于回源获取数据
     * @param <K>       key 类型
     * @param <V>       value 类型
     * @return 缓存或加载得到的值
     */
    public <K, V> V getOrLoad(String cacheName, K key, Function<K, V> loader) {
        Cache cache = getRequiredCache(cacheName);
        return cache.get(key, () -> loader.apply(key));
    }

    /**
     * 从缓存获取值，如果不存在则返回 null。
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param <K>       key 类型
     * @param <V>       value 类型
     * @return 缓存中的值或 null
     */
    public <K, V> V get(String cacheName, K key) {
        Cache.ValueWrapper wrapper = getRequiredCache(cacheName).get(key);
        return wrapper != null ? (V) wrapper.get() : null;
    }

    /**
     * 将指定值放入缓存。
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @param value     缓存值
     * @param <K>       key 类型
     * @param <V>       value 类型
     */
    public <K, V> void put(String cacheName, K key, V value) {
        getRequiredCache(cacheName).put(key, value);
    }

    /**
     * 判断缓存中是否存在指定键。
     *
     * @param cacheName 缓存名称
     * @param key       缓存键
     * @return true 如果存在；false 否则
     */
    public boolean contains(String cacheName, Object key) {
        return get(cacheName, key) != null;
    }

    /**
     * 从缓存中删除指定键。
     *
     * @param cacheName 缓存名称
     * @param key       要删除的键
     */
    public void evict(String cacheName, Object key) {
        getRequiredCache(cacheName).evict(key);
    }

    /**
     * 清空指定缓存。
     *
     * @param cacheName 缓存名称
     */
    public void clear(String cacheName) {
        getRequiredCache(cacheName).clear();
    }

    @SuppressWarnings("unchecked")
    public boolean putIfAbsent(String cacheName, String key, String value) {
        com.github.benmanes.caffeine.cache.Cache<String, String> nativeCache = getNativeCaffeine(cacheName);
        return nativeCache.asMap().putIfAbsent(key, value) == null;
    }


    public boolean compareAndSet(String cacheName, String key, String expectedValue, String newValue) {
        com.github.benmanes.caffeine.cache.Cache<String, String> nativeCache = getNativeCaffeine(cacheName);
        return nativeCache.asMap().replace(key, expectedValue, newValue);
    }

    @SuppressWarnings("unchecked")
    private <K, V> com.github.benmanes.caffeine.cache.Cache<K, V> getNativeCaffeine(String cacheName) {
        Cache cache = getRequiredCache(cacheName);
        return (com.github.benmanes.caffeine.cache.Cache<K, V>) cache.getNativeCache();
    }
}
