package com.moment.service;


import com.moment.dto.DishDTO;
import com.moment.dto.DishPageQueryDTO;
import com.moment.entity.Dish;
import com.moment.result.PageResult;
import com.moment.vo.DishVO;

import java.util.List;

public interface DishService {
    void addDish(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO queryDTO);



    void deleteDish(List<Long> ids);

    List<Dish> getByCategoryId(Long cateId);

    DishVO getById(Long id);

    void updateStatus(Integer status,Long id);

    void updateDishvo(DishDTO dishDTO);
    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
