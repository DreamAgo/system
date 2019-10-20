package cn.loverot.system.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.loverot.basic.utils.RedisUtil;
import cn.loverot.system.constant.CacheConstant;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 密码加密工具类
 */
@Component
public class PasswordUtil {

    private static RedisUtil redisUtil;
    @Autowired
    private RedisUtil redisUtil_;

    @PostConstruct
    public void init() {
        PasswordUtil.redisUtil = redisUtil_;
    }

    public static String encrypt(String username,String passsword,String salt){
      return   SecureUtil.sha1(username+passsword+salt);
    }

    public static String RandSalt(){
       return RandomUtil.randomString(8);
    }

    public static void setSessionExpire(String token, String newAuthorization) {
        // 设置超时时间
        redisUtil.set(CacheConstant.PREFIX_USER_TOKEN + token, newAuthorization);
        redisUtil.expire(CacheConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME *2);
    }

}

