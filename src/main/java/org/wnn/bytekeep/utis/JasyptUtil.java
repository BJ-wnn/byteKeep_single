package org.wnn.bytekeep.utis;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * Jasypt 工具类
 * @author NanNan Wang
 */
public class JasyptUtil {

    /**
     * 创建 Jasypt 加密器
     *
     * @param password  加密密钥
     * @param algorithm 加密算法
     * @return PooledPBEStringEncryptor 实例
     */
    public static PooledPBEStringEncryptor createEncryptor(String password, String algorithm) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("JASYPT_ENCRYPTOR_PASSWORD 不能为空，请设置环境变量或传入参数");
        }

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password); // 加密密钥
        config.setAlgorithm(algorithm); // 指定加密算法
        config.setKeyObtentionIterations("1000"); // 迭代次数，增加破解难度
        config.setPoolSize("1"); // 线程池大小
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // 随机盐
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator"); // 随机 IV
        config.setStringOutputType("base64"); // 输出类型 base64
        encryptor.setConfig(config);
        return encryptor;
    }

    /**
     * 加密
     *
     * @param plainText 明文
     * @param password  加密密钥
     * @param algorithm 加密算法
     * @return 加密后的字符串（Base64 编码）
     */
    public static String encrypt(String plainText, String password, String algorithm) {
        PooledPBEStringEncryptor encryptor = createEncryptor(password, algorithm);
        return encryptor.encrypt(plainText);
    }

    /**
     * 解密
     *
     * @param encryptedText 密文（ENC(...) 格式）
     * @param password      加密密钥
     * @param algorithm     加密算法
     * @return 解密后的明文
     */
    public static String decrypt(String encryptedText, String password, String algorithm) {
        PooledPBEStringEncryptor encryptor = createEncryptor(password, algorithm);
        return encryptor.decrypt(encryptedText);
    }
}
