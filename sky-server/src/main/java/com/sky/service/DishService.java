package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

public interface DishService {

    /**
     * 新增菜品和口味
     * @param dto
     */
    public void saveWithFlavor(DishDTO dto);

    /**
     * 菜品分页查询
     *
     * @param dto
     * @return
     */
    PageResult pageQuary(DishPageQueryDTO dto);
}
