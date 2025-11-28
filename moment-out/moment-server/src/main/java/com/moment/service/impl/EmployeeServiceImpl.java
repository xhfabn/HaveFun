package com.moment.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.moment.constant.MessageConstant;
import com.moment.constant.PasswordConstant;
import com.moment.constant.StatusConstant;
import com.moment.dto.EmployeeDTO;
import com.moment.dto.EmployeeLoginDTO;
import com.moment.dto.EmployeePageQueryDTO;
import com.moment.entity.Employee;
import com.moment.exception.AccountLockedException;
import com.moment.exception.AccountNotFoundException;
import com.moment.exception.PasswordErrorException;
import com.moment.mapper.EmployeeMapper;
import com.moment.result.PageResult;
import com.moment.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传过来的密码进行md5加密处理

        password=DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setStatus(StatusConstant.ENABLE);

        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
////后期动态获取
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.insertEm(employee);


    }

    @Override
    public PageResult queryEmp(EmployeePageQueryDTO employeePageQueryDTO) {

        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee> page= employeeMapper.queryEmp(employeePageQueryDTO);

        long total = page.getTotal();
        List<Employee> records=page.getResult();
        PageResult pageResult=new PageResult(total,records);

        return pageResult;
    }

    @Override
    public void empStatus(Integer status, Long id) {
//        Employee employee= employeeMapper.getById(id);
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.chEmpStatus(employee);

    }

    @Override
    public Employee getById(Long id) {
        return  employeeMapper.getById(id);
    }

    @Override
    public void editEmp(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);

//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.chEmpStatus(employee);
    }




}
