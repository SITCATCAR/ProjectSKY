package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "商店相关接口")
@Slf4j
public class ShopController {

   public static final String shopStatus="SHOP_STATUS";
    @Autowired
    RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺状态为: {}",status==1? "营业中" : "打烊了");
        redisTemplate.opsForValue().set(shopStatus,status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status= (Integer)redisTemplate.opsForValue().get(shopStatus);

        return Result.success(status);
    }

}
