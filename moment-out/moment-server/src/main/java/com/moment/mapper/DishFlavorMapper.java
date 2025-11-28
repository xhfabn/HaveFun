package com.moment.mapper;

import com.moment.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void insert(List<DishFlavor> flavors);

    @Delete("delete from dish_flavor where dish_id=#{id}")
    void deleteDishfavorById(Long id);

    void deleteDishfavorByIds(List<Long> ids);

    @Select("select * from dish_flavor where dish_id=#{id}")
    List<DishFlavor> getByDishId(Long id);


    void updateDishFlavor(Long dishId, DishFlavor dishFlavor);
}
