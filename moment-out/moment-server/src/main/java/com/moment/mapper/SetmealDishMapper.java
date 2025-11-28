package com.moment.mapper;

import com.moment.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    List<Long> getSetmealIdByDishId(List<Long> dishIds);

    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    void inert(SetmealDish setmealDish);

    @Select("select * from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> getmealDishBysetmealId(Long id);

    void delete(List<Long> ids);



    @Delete("delete from setmeal_dish where setmeal_id=#{id}")
    void deleteById(Long setmealId);

    void inertDish(List<SetmealDish> setmealDishList);
}
