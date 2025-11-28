package com.moment.mapper;

import com.github.pagehelper.Page;
import com.moment.annotation.AutoFill;
import com.moment.dto.EmployeePageQueryDTO;
import com.moment.entity.Employee;
import com.moment.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user)" +
            " values (#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insertEm(Employee employee);


    Page<Employee> queryEmp(EmployeePageQueryDTO employeePageQueryDTO);

    @Select("select * from employee where id=#{id}")
    Employee getById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void chEmpStatus(Employee employee);


//    void editEmp(EmployeeDTO employeeDTO);
}
