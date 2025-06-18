package org.wnn.bytekeep.demo.dao.db2;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.wnn.bytekeep.demo.dao.db2.entity.EmployeeEntity;

/**
 * @author NanNan Wang
 */
@Mapper
public interface EmployeesMapper {

    long insertEmployee(@Param("employee") EmployeeEntity employee);


}
