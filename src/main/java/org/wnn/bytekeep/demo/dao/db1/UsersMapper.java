package org.wnn.bytekeep.demo.dao.db1;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.wnn.bytekeep.demo.dao.db1.entity.UserEntity;

/**
 * @author NanNan Wang
 */
@Mapper
public interface UsersMapper {

    /**
     * 插入用户数据
     * @param user 用户对象
     */
    long insertUser(@Param("user")UserEntity user);


}
