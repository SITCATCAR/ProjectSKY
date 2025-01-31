package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties WXP;
    @Autowired
    UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO dto) {
        //API获得openid
        String openid = GetOpenId(dto);

        //是否为新用户
        User user = userMapper.geByOpenid(openid);
        //新用户自动注册
        if(user==null){
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回用户

        return user;
    }

    private String GetOpenId(UserLoginDTO dto) {
        Map<String,String> param=new HashMap<>();
        param.put("appid",WXP.getAppid());
        param.put("secret",WXP.getSecret());
        param.put("js_code", dto.getCode());
        param.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN,param);
        //判断API是否成功
        JSONObject jsonObject=JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");
        if(openid==null){
            log.error("API请求错误: {}",jsonObject);
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        log.info("成功返回数据: {}",jsonObject);
        return openid;
    }
}
