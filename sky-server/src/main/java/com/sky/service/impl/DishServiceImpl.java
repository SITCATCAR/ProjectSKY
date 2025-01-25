package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
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
}
