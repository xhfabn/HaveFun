package com.moment.controller.admin;


import com.moment.dto.CategoryDTO;
import com.moment.dto.CategoryPageQueryDTO;
import com.moment.entity.Category;
import com.moment.result.PageResult;
import com.moment.result.Result;
import com.moment.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Tag(name = "分类相关的接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @PostMapping
    @Operation(summary = "添加分类")
    public Result saveCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("添加分类:{}",categoryDTO);
        categoryService.saveCate(categoryDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "分类，分页查询")
    public Result<PageResult> pageQue(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("开始分页查询：{}",categoryPageQueryDTO);
        PageResult pageResult= categoryService.pageQue(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "启用，禁用分类")
    public Result chStatus(@PathVariable Integer status,Long id){
        log.info("启用，禁用分类：{}",status);
        categoryService.chStatus(status,id);
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "根据id删除分类")
    public Result delete( Long id){
        log.info("删除分类：{}",id);
        categoryService.delete(id);
        return Result.success();
    }
    @GetMapping("/list")
    @Operation(summary = "根据类型查询分类")
    public Result<List<Category>> categoryQue(Integer type){
        log.info("根据类型查询分类");
        List<Category> list= categoryService.categoryQue(type);
        return Result.success(list);
    }

    @PutMapping
    @Operation(summary = "修改分类")
    public Result<String> updateCate(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类：{}",categoryDTO);
        categoryService.updateCate(categoryDTO);
        return Result.success();
    }





}
