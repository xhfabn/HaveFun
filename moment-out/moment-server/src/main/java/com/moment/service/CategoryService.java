package com.moment.service;


import com.moment.dto.CategoryDTO;
import com.moment.dto.CategoryPageQueryDTO;
import com.moment.entity.Category;
import com.moment.result.PageResult;

import java.util.List;


public interface CategoryService {

    void saveCate(CategoryDTO categoryDTO);

    PageResult pageQue(CategoryPageQueryDTO categoryPageQueryDTO);

    void chStatus(Integer status, Long id);

    void delete(Long id);

    List<Category> categoryQue(Integer type);

    void updateCate(CategoryDTO categoryDTO);
}
