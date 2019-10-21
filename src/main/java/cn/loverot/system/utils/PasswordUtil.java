package cn.loverot.system.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.loverot.basic.utils.RedisUtil;
import cn.loverot.basic.utils.SpringUtil;
import cn.loverot.system.constant.CacheConstant;
import cn.loverot.system.constant.Const;
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

    public static String getToken(){
      return SpringUtil.getRequest().getHeader(Const.X_ACCESS_TOKEN);
    }

    /**
     * 取随机盐值
     * @return
     */
    public static String RandSalt(){
       return RandomUtil.randomString(8);
    }

    /**
     * 更新token过期时间
     * @param token
     * @param newAuthorization
     */
    public static void setSessionExpire(String token, String newAuthorization) {
        // 设置超时时间
        redisUtil.set(CacheConstant.PREFIX_USER_TOKEN + token, newAuthorization);
        redisUtil.expire(CacheConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME *2);
    }

    /**
     * 删除指定token
     * @param token
     */
    public static void delToken(String token) {
        redisUtil.delete(CacheConstant.PREFIX_USER_TOKEN + token);
    }

    /**
     * 删除当前登录用户token
     */
    public static void delToken() {
        delToken(getToken());
    }

}

