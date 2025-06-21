package org.wnn.bytekeep.demo.service;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wnn.bytekeep.demo.dao.db1.UsersMapper;
import org.wnn.bytekeep.demo.dao.db1.entity.UserEntity;
import org.wnn.bytekeep.demo.dao.db2.EmployeesMapper;
import org.wnn.bytekeep.demo.dao.db2.entity.EmployeeEntity;

import java.util.Date;
import java.util.Random;

/**
 * @author NanNan Wang
 */
@Service
@RequiredArgsConstructor
public class DemoInsertService {

    private final UsersMapper usersMapper;
    private final EmployeesMapper employeesMapper;

    private Random random = new Random();

    @Transactional
    public void insertUserAndEmployee() {
        UserEntity user = new UserEntity()
                .setUsername("wangnannan"+ random.nextInt())
                .setPassword("123")
                .setPhone("13xxxx12311")
                .setEmail("123@126.com"+ random.nextInt());
        usersMapper.insertUser(user);
        EmployeeEntity employee = new EmployeeEntity()
                .setEmployeeNumber("Exxx123" + random.nextInt())
                .setName("wangnannan" + random.nextInt())
                .setGender(1)
                .setDepartmentId(1L)
                .setPosition("程序员")
                .setEmail("123@126.com" + random.nextInt())
                .setPhone("13xxxx12311")
                .setHireDate(new Date())
                .setCreatedAt(new Date())
                .setUpdatedAt(new Date());
        employeesMapper.insertEmployee(employee);
//        throw new RuntimeException("测试事务");
    }
}
