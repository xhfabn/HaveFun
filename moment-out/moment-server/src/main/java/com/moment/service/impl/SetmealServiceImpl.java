package com.moment.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.moment.dto.SetmealDTO;
import com.moment.dto.SetmealPageQueryDTO;
import com.moment.entity.Setmeal;
import com.moment.entity.SetmealDish;
import com.moment.mapper.SetmealDishMapper;
import com.moment.mapper.SetmealMapper;
import com.moment.result.PageResult;
import com.moment.service.SetmealService;
import com.moment.vo.DishItemVO;
import com.moment.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        log.info("id值为：{}",setmealDTO.getId());
        BeanUtils.copyProperties(setmealDTO,setmeal);
//        操作setmeal数据库
        setmealMapper.insert(setmeal);
//        获取setmeal数据库与setmeal_dish数据库的连接部分
        Long setmealId=setmealMapper.selectId(setmeal);
        List<SetmealDish> setmealDishList=setmealDTO.getSetmealDishes();
        if(setmeal!=null){
            for (SetmealDish dish : setmealDishList) {
                SetmealDish setmealDish=new SetmealDish();
                setmealDish.setSetmealId(setmealId);
                setmealDish.setDishId(dish.getDishId());
                setmealDish.setCopies(dish.getCopies());
                setmealDish.setName(dish.getName());
                setmealDish.setPrice(dish.getPrice());
                setmealDish.setId(dish.getId());
                setmealDishMapper.inert(setmealDish);
            }
        }



    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> setmealVOPage=setmealMapper.pageQuery(setmealPageQueryDTO);
       return new  PageResult(setmealVOPage.getTotal(),setmealVOPage.getResult());
    }

    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO=setmealMapper.getById(id);
        List<SetmealDish> setmealDishList=setmealDishMapper.getmealDishBysetmealId(id);
        setmealVO.setSetmealDishes(setmealDishList);
        return setmealVO ;
    }

    @Override
    public void deleteSetmeal(List<Long> ids) {
//        删除数据包括两个数据库
        setmealMapper.delete(ids);
        setmealDishMapper.delete(ids);
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        setmealMapper.changeStatus(status,id);
    }

    @Override
    public void updateSeatMeal(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        Long setmealId=setmealMapper.selectId(setmeal);
        setmealDishMapper.deleteById(setmealId);
        List<SetmealDish> setmealDishList=setmealDTO.getSetmealDishes();
        if(setmealDishList!=null&&setmealDishList.size()>0){
            setmealDishList.forEach(setmealDish ->
                    setmealDish.setSetmealId(setmealId));
            setmealDishMapper.inertDish(setmealDishList);
        }

    }
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
