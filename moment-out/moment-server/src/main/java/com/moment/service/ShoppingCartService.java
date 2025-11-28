package com.moment.service;

import com.moment.dto.ShoppingCartDTO;
import com.moment.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    void insertShops(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> queryShop();

    void deleteOne(ShoppingCartDTO shoppingCartDTO);

    void deleteAll();
}
