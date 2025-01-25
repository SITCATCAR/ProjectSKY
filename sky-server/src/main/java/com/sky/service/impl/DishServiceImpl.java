package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dto) {
        //菜品插入一条
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto,dish);

        dishMapper.insert(dish);

        Long id = dish.getId();
        //口味插入
        List<DishFlavor> flavors=dto.getFlavors();
        if(!flavors.isEmpty()){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });

            dishFlavorMapper.insertBatch(flavors);
        }

    }

    @Override
    public PageResult pageQuary(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        try (Page<DishVO> page = dishMapper.pageQuary(dto)) {
            return new PageResult(page.getTotal(), page.getResult());
        }
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //状态启用不能删除
        for (Long id : ids) {
           Dish dish =dishMapper.getById(id);
           if(dish.getStatus() == StatusConstant.ENABLE)
               throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        //关联套餐不能删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(ids);
        if(!setmealIds.isEmpty())
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        //删除dish表

        for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除dish_flavor表
            dishFlavorMapper.deleteByDishId(id);
        }



    }
}
