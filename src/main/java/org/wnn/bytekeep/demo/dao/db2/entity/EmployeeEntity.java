package org.wnn.bytekeep.demo.dao.db2.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author NanNan Wang
 */
@Data
@Accessors(chain = true)
public class EmployeeEntity {
    private Long id;
    private String employeeNumber; // 员工编号
    private String name;           // 姓名
    private Integer gender;        // 性别（0:女, 1:男）
    private Long departmentId;     // 所属部门ID
    private String position;       // 职位
    private String email;          // 邮箱
    private String phone;          // 联系电话
    private Date hireDate;         // 入职日期
    private Date createdAt;        // 创建时间
    private Date updatedAt;        // 最后更新时间
}
