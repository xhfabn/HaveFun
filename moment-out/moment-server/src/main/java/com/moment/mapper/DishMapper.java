package com.moment.mapper;

import com.github.pagehelper.Page;
import com.moment.annotation.AutoFill;
import com.moment.dto.DishPageQueryDTO;
import com.moment.entity.Dish;
import com.moment.enumeration.OperationType;
import com.moment.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);


    Page<DishVO> pageQuery(DishPageQueryDTO queryDTO);

    @Select("select * from dish where id=#{id}")
    Dish getDishById(Long id);


    @Delete("delete  from dish where id=#{id}")
    void deleteDishById(Long id);

    void deleteDishByIds(List<Long> ids);


    List<Dish> selectByCateId(Dish dish);

    DishVO getById(Long id);

    @Update("update dish set status=#{status} where id=#{id}")
    void updateStatus(Integer status,Long id);

    @AutoFill(value = OperationType.UPDATE)
    void updateDish(Dish dish);

    Integer countByMap(Map map);
}
