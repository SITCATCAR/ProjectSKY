package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/admin/setmeal")
@RestController
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping()
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO dto){
        setmealService.saveWithDish(dto);
        return Result.success();
    }

    /**
     * 分页查询
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO dto){
        PageResult pageResult=setmealService.pageQuery(dto);
        return Result.success(pageResult);
    }
}
