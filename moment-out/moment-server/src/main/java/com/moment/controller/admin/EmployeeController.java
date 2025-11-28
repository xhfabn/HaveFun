package com.moment.controller.admin;

import com.moment.constant.JwtClaimsConstant;
import com.moment.dto.EmployeeDTO;
import com.moment.dto.EmployeeLoginDTO;
import com.moment.dto.EmployeePageQueryDTO;
import com.moment.entity.Employee;
import com.moment.properties.JwtProperties;
import com.moment.result.PageResult;
import com.moment.result.Result;
import com.moment.service.EmployeeService;
import com.moment.utils.JwtUtil;
import com.moment.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name="员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "员工退出")
    public Result<String> logout() {
        return Result.success();
    }


    @PostMapping
    @Operation(summary = "添加员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增的员工为：{}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "员工的分页查询")
    public Result<PageResult> queryEmp(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("开始分页查询");
        PageResult pageResult= employeeService.queryEmp(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("status/{status}")
    @Operation(summary = "启用，禁用员工的账号")
    public Result empStatus(@PathVariable Integer status,Long id){
        log.info("启用，禁用员工账号");
        employeeService.empStatus(status,id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询员工")
    public Result<Employee> getByID(@PathVariable Long id){
        log.info("查询的员工为：{}",id);
        Employee employee= employeeService.getById(id);
        return Result.success(employee);
    }

    @PutMapping
    @Operation(summary = "编辑员工的信息")
    public Result editEmp(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工的信息：{}",employeeDTO);
        employeeService.editEmp(employeeDTO);
        return Result.success();
    }



}
