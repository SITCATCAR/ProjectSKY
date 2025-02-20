package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询套餐id
     * @param dishIds
     * @return
     */
    //select id from setmeal where dish_id in (1,2,3)
    List<Long> getSetmealIdsByDishId(List<Long> dishIds);

    /**
     * 批量保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    @Delete("delete from setmeal_dish where setmeal_id=#{id}")
    void deleteBySetmealId(Long id);


    @Select("select * from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> getBySetmealId(Long id);
}
