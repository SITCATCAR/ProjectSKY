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

import java.util.ArrayList;
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

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //id查菜品
        Dish dish = dishMapper.getById(id);
        //id查口味
        List<DishFlavor> dishFlavors =dishFlavorMapper.getByDishId(id);
        //封装到Vo
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dto) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dto,dish);
        //修改菜品基本信息
        dishMapper.update(dish);
        //删除原有口味数据
        dishFlavorMapper.deleteByDishId(dto.getId());
        //重新插入口味

        List<DishFlavor> flavors = dto.getFlavors();
        if(!flavors.isEmpty()){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dto.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish=Dish.builder()
                        .categoryId(categoryId)
                        .status(StatusConstant.ENABLE)
                        .build();
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

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
