package org.wnn.bytekeep.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wnn.bytekeep.demo.dao.db1.UsersMapper;
import org.wnn.bytekeep.demo.dao.db1.entity.UserEntity;
import org.wnn.bytekeep.demo.dao.db2.EmployeesMapper;
import org.wnn.bytekeep.demo.dao.db2.entity.EmployeeEntity;

import java.util.Date;

/**
 * @author NanNan Wang
 */
@Service
@RequiredArgsConstructor
public class DemoInsertService {

    private final UsersMapper usersMapper;
    private final EmployeesMapper employeesMapper;

    @Transactional
    public void insertUserAndEmployee() {
        UserEntity user = new UserEntity()
                .setUsername("wangnannan")
                .setPassword("123")
                .setPhone("13xxxx12311")
                .setEmail("123@126.com");
        usersMapper.insertUser(user);
        EmployeeEntity employee = new EmployeeEntity()
                .setEmployeeNumber("Exxx123")
                .setName("wangnannan")
                .setGender(1)
                .setDepartmentId(1L)
                .setPosition("程序员")
                .setEmail("123@126.com")
                .setPhone("13xxxx12311")
                .setHireDate(new Date())
                .setCreatedAt(new Date())
                .setUpdatedAt(new Date());
        employeesMapper.insertEmployee(employee);
        throw new RuntimeException("测试事务");
    }
}
