package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("ALL")
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api("商店相关接口")
@Slf4j
public class ShopController {

   public static final String shopStatus="SHOP_STATUS";
    @Autowired
    RedisTemplate redisTemplate;


    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status= (Integer)redisTemplate.opsForValue().get(shopStatus);

        return Result.success(status);
    }

}
