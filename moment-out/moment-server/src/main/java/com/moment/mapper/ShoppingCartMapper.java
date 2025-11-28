package com.moment.mapper;

import com.moment.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {


    List<ShoppingCart> list(ShoppingCart shoppingCart);


    void updateNumber(ShoppingCart cart);

    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id=#{id}")
    List<ShoppingCart> queryAll(Long id);

    void deleteOne(ShoppingCart shoppingCart);
    @Delete("delete from shopping_cart where user_id=#{id}")
    void deleteAll(Long id);
}
