package com.moment.controller.admin;

import com.moment.dto.DishDTO;
import com.moment.dto.DishPageQueryDTO;
import com.moment.entity.Dish;
import com.moment.result.PageResult;
import com.moment.result.Result;
import com.moment.service.DishService;
import com.moment.vo.DishVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Tag(name = "菜品相关的接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @Operation(summary = "新增菜品")
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("开始新增菜品：{}",dishDTO);
        dishService.addDish(dishDTO);
        String key="dish_"+dishDTO.getCategoryId();
        redisTemplate.delete(key);
        return Result.success();
    }
    @GetMapping("/page")
    @Operation(summary = "菜品的分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO queryDTO){
        log.info("开始菜品的分页查询");
        PageResult pageResult=dishService.pageQuery(queryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @Operation(summary = "批量删除菜品")
    public Result deleteDish(@RequestParam List<Long> ids){
        log.info("批量删除菜品：{}",ids);
        dishService.deleteDish(ids);
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long cateId){
        log.info("根据分类id查询菜品：{}",cateId);
       List<Dish> list=dishService.getByCategoryId(cateId);
        return Result.success(list);
    }
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询商品");
        DishVO dishVO=dishService.getById(id);
        return Result.success(dishVO);
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "菜品的起售，停售")
    public Result dishStatus(@PathVariable Integer status,Long id){
        log.info("菜品的起售，停售：{}{}",status,id);
        dishService.updateStatus(status,id);
        cleanCache("dish_*");
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO ){
        log.info("修改菜品：{}",dishDTO);
        dishService.updateDishvo(dishDTO);
        cleanCache("dish_*");

        return Result.success();
    }

    public void cleanCache(String pattern){
        Set keys=redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
        log.info("已清除所有的缓存");
    }
}
