package com.moment.service;

import com.moment.dto.EmployeeDTO;
import com.moment.dto.EmployeeLoginDTO;
import com.moment.dto.EmployeePageQueryDTO;
import com.moment.entity.Employee;
import com.moment.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);


    PageResult queryEmp(EmployeePageQueryDTO employeePageQueryDTO);

    void empStatus(Integer status, Long id);

    Employee getById(Long id);

    void editEmp(EmployeeDTO employeeDTO);


}
