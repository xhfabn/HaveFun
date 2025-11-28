package com.moment.mapper;

import com.github.pagehelper.Page;
import com.moment.annotation.AutoFill;
import com.moment.dto.SetmealPageQueryDTO;
import com.moment.entity.Setmeal;
import com.moment.enumeration.OperationType;
import com.moment.vo.DishItemVO;
import com.moment.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @Select("select id from setmeal where name=#{name}")
    Long selectId(Setmeal setmeal);

    SetmealVO getById(Long id);

    void delete(List<Long> ids);

    @Update("update setmeal set status=#{status} where id=#{id}")
    void changeStatus(Integer status, Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);


    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    Integer countByMap(Map map);

}
