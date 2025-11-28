package com.moment.service;

import com.moment.dto.SetmealDTO;
import com.moment.dto.SetmealPageQueryDTO;
import com.moment.entity.Setmeal;
import com.moment.result.PageResult;
import com.moment.vo.DishItemVO;
import com.moment.vo.SetmealVO;

import java.util.List;


public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO getById(Long id);

    void deleteSetmeal(List<Long> ids);

    void changeStatus(Integer status, Long id);

    void updateSeatMeal(SetmealDTO setmealDTO);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
