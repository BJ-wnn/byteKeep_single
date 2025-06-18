package org.wnn.bytekeep.demo.dao.db1.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author NanNan Wang
 */
@Data
@Accessors(chain = true)
public class UserEntity {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
}
