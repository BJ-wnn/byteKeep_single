package org.wnn.bytekeep.core.idempotent;

/**
 * @author NanNan Wang
 */
public interface IdempotentTokenService {

    /**
     * 幂等性验证：如果 token 存在，表示重复提交
     * @param token 幂等性 token
     * @return true 表示首次处理；false 表示已处理
     */
    boolean tryUseToken(String token);

}
