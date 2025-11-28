package com.moment.service.impl;

import com.moment.context.BaseContext;
import com.moment.dto.ShoppingCartDTO;
import com.moment.entity.ShoppingCart;
import com.moment.mapper.DishMapper;
import com.moment.mapper.SetmealMapper;
import com.moment.mapper.ShoppingCartMapper;
import com.moment.service.ShoppingCartService;
import com.moment.vo.DishVO;
import com.moment.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void insertShops(ShoppingCartDTO shoppingCartDTO) {
        //在添加购物车商品前首先要判断是否有商品 有商品是对应数据表中的数据加1
        //传入的数据中有dishid setmealid 中至少有一个
        //先查询数据库中是否有该数据
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
        //动态获取到数据库中的数据，只有一条数据，如果不为空 说明
        if(list!=null&&list.size()>0){
            ShoppingCart cart=list.get(0);
            //将得到的number加1
            cart.setNumber(cart.getNumber()+1);

            //更新数据库
            shoppingCartMapper.updateNumber(cart);
        }else {
            //如果没有该数据，就要设置cart的值，传入进行修改
            Long dishId=shoppingCart.getDishId();
            if(dishId!=null){
                //说明传入的是dish,要通过dishid的值dish表中进行查找dish的图片，价格，名称
                DishVO dishVO=dishMapper.getById(dishId);
                shoppingCart.setAmount(dishVO.getPrice());
                shoppingCart.setName(dishVO.getName());
                shoppingCart.setImage(dishVO.getImage());
                //查看所有的数据是否封装，flavor的数据在copy过程中由前端传过来
                //因为是第一次插入，number要设置成1
//            shoppingCart.setNumber(1);
//            shoppingCart.setCreateTime(LocalDateTime.now());
//
//            shoppingCartMapper.insert(shoppingCart);
            }else {
                //不是dish传入的就是mealdish
                Long mealId=shoppingCart.getSetmealId();
                SetmealVO setmealVO = setmealMapper.getById(mealId);
                shoppingCart.setAmount(setmealVO.getPrice());
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }


    }

    @Override
    public List<ShoppingCart> queryShop() {
        Long id = BaseContext.getCurrentId();
        //通过id来判定用户
        return shoppingCartMapper.queryAll(id);
    }

    @Override
    public void deleteOne(ShoppingCartDTO shoppingCartDTO) {

        //先要判断数据中的number是否是1，如果是就直接删除，如果不是则应该number减一
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        ShoppingCart cart=list.get(0);
        if(cart.getNumber()==1){
            shoppingCartMapper.deleteOne(shoppingCart);
        }else {
            cart.setNumber(cart.getNumber()-1);
            shoppingCartMapper.updateNumber(cart);
        }

    }

    @Override
    public void deleteAll() {
        Long id=BaseContext.getCurrentId();
        shoppingCartMapper.deleteAll(id);
    }
}
