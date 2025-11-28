package com.moment.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.moment.constant.MessageConstant;
import com.moment.constant.StatusConstant;
import com.moment.dto.DishDTO;
import com.moment.dto.DishPageQueryDTO;
import com.moment.entity.Dish;
import com.moment.entity.DishFlavor;
import com.moment.exception.DeletionNotAllowedException;
import com.moment.mapper.CategoryMapper;
import com.moment.mapper.DishFlavorMapper;
import com.moment.mapper.DishMapper;
import com.moment.mapper.SetmealDishMapper;
import com.moment.result.PageResult;
import com.moment.service.DishService;
import com.moment.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

/*
    添加菜品
*/
    @Transactional
    public void addDish(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.insert(dish);
        Long id = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&&flavors.size()>0){
            flavors.forEach(dishFlavor ->
                    dishFlavor.setDishId(id));
            dishFlavorMapper.insert(flavors);
        }

    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(),queryDTO.getPageSize());
        Page<DishVO> page=dishMapper.pageQuery(queryDTO);

        Long total=page.getTotal();
        List<DishVO> records=page.getResult();
        return new PageResult(total,records);
    }

    @Override
    public void deleteDish(List<Long> ids) {
//        批量删除菜品，首先要判定菜品是否在起售中的菜品，如果是起售中的菜品，不能删除
        for (Long id : ids) {
            Dish dish=dishMapper.getDishById(id);
            if(dish.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
//        第二还要判断该菜品是否关联到套餐，如果关联到了不能删除
        List<Long> dishIds= setmealDishMapper.getSetmealIdByDishId(ids);
        if(dishIds!=null&&dishIds.size()>0){
//            说明关联到套餐了
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

        }
//        第三步是要删除dish数据库中的数据以及dishflavor数据库中的数据
//        for (Long id : ids) {
//            dishMapper.deleteDishById(id);
////            删除dishfavor中的数据
//            dishFlavorMapper.deleteDishfavorById(id);
//        }


//    批量删除dish和dish_flavor数据库中的数据
        dishMapper.deleteDishByIds(ids);
        dishFlavorMapper.deleteDishfavorByIds(ids);
    }

    @Override
    public List<Dish> getByCategoryId(Long cateId) {
        Dish dish= Dish.builder()
                .categoryId(cateId)
                .status(StatusConstant.ENABLE)
                .build();
       return dishMapper.selectByCateId(dish);
    }

    @Override
    public DishVO getById(Long id) {
        DishVO dishVO= dishMapper.getById(id);
        List<DishFlavor> list=dishFlavorMapper.getByDishId(id);
        Long cateId=dishVO.getCategoryId();
        String categoryName=categoryMapper.getNameById(cateId);
        dishVO.setFlavors(list);
        dishVO.setCategoryName(categoryName);
        return dishVO;
    }

    @Override
    public void updateStatus(Integer status,Long id) {
        dishMapper.updateStatus(status,id);
    }

    @Transactional
    public void updateDishvo(DishDTO dishDTO) {
        Dish dish =new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateDish(dish);

        dishFlavorMapper.deleteDishfavorById(dishDTO.getId());
        List<DishFlavor> flavors=dishDTO.getFlavors();
        if(flavors!=null&&flavors.size()>0){
            flavors.forEach(dishFlavor ->
                    dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insert(flavors);
        }
    }
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.selectByCateId(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


}
