package cn.loverot.system.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;

/**
 * 密码加密工具类
 */
public class PasswordUtil {

    public static String encrypt(String username,String passsword,String salt){
      return   SecureUtil.sha1(username+passsword+salt);
    }

    public static String RandSalt(){
       return RandomUtil.randomString(8);
    }

}

