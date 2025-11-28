package com.moment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.moment.constant.StatusConstant;
import com.moment.dto.CategoryDTO;
import com.moment.dto.CategoryPageQueryDTO;
import com.moment.entity.Category;
import com.moment.mapper.CategoryMapper;
import com.moment.result.PageResult;
import com.moment.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

        //添加分类
    @Override
    public void saveCate(CategoryDTO categoryDTO) {
        Category category =new Category();
        BeanUtils.copyProperties(categoryDTO,category);

//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateUser(BaseContext.getCurrentId());

        category.setStatus(StatusConstant.DISABLE);

        categoryMapper.insertCate(category);
    }

    @Override
    public PageResult pageQue(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> page=categoryMapper.queryCate(categoryPageQueryDTO);
        Long total=page.getTotal();
        List<Category> records=page.getResult();

        PageResult pageResult=new PageResult(total,records);

        return pageResult;
    }

    @Override
    public void chStatus(Integer status, Long id) {
        Category category=Category.builder()
                .status(status)
                .id(id)
                .build();
        categoryMapper.update(category);
    }

    @Override
    public void delete(Long id) {
        categoryMapper.delete(id);
    }

    @Override
    public List<Category> categoryQue(Integer type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null, Category::getType, type)
                .eq(Category::getStatus, StatusConstant.ENABLE)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public void updateCate(CategoryDTO categoryDTO) {
        Category category=new Category();
        BeanUtils.copyProperties(categoryDTO,category);

//        category.setUpdateUser(BaseContext.getCurrentId());
//        category.setUpdateTime(LocalDateTime.now());

        categoryMapper.update(category);
    }


}
