package com.moment.controller.user;

import com.moment.dto.ShoppingCartDTO;
import com.moment.entity.ShoppingCart;
import com.moment.result.Result;
import com.moment.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Tag(name = "购物车相关接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @Operation(summary = "添加购物车接口")
    public Result addDish(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加商品为：{}",shoppingCartDTO);
        shoppingCartService.insertShops(shoppingCartDTO);
        return Result.success();
    }
    @GetMapping("/list")
    @Operation(summary = "查看购物车")
    public Result<List<ShoppingCart>> queryShop(){
        log.info("查看购物车");
        List<ShoppingCart> list=shoppingCartService.queryShop();
        return Result.success(list);
    }

    @PostMapping("/sub")
    @Operation(summary = "删除购物车中的一个商品")
    public Result deleteOne(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车中的一个商品：{}",shoppingCartDTO);
        shoppingCartService.deleteOne(shoppingCartDTO);
        return Result.success();
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空购物车")
    public Result deleteAll(){
        log.info("清空购物车");
        shoppingCartService.deleteAll();
        return Result.success();
    }
}
