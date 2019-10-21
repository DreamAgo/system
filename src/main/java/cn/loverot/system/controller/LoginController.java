package cn.loverot.system.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.loverot.basic.utils.RedisUtil;
import cn.loverot.common.entity.ResultResponse;
import cn.loverot.common.exception.HsException;
import cn.loverot.system.constant.CacheConstant;
import cn.loverot.system.entity.LoginLog;
import cn.loverot.system.entity.User;
import cn.loverot.system.service.ILoginLogService;
import cn.loverot.system.service.IUserService;
import cn.loverot.system.utils.JwtUtil;
import cn.loverot.system.utils.PasswordUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huise
 */
@Validated
@RestController
public class LoginController extends BaseController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ILoginLogService loginLogService;

    @PostMapping("login")
    public ResultResponse login(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password,
            @NotBlank(message = "{required}") String randCode,
            boolean rememberMe) throws HsException {
//        if (!this.checkRandCode()) {
//            throw new HsException("验证码错误!");
//        }
        User user = userService.findByName(username);
        if(ObjectUtil.isNull(user)){
            throw new AuthorizationException("用户名不存在!");
        }
        ResultResponse resultResponse = userService.checkUserIsEffective(user);
        if(!resultResponse.isSuccess()){
            return resultResponse;
        }
        //验证密码是否正确
        String userPassword = user.getPassword();
        String inPassword = PasswordUtil.encrypt(username, password, user.getSalt());
        if(!userPassword.equals(inPassword)){
            throw new AuthorizationException("用户名或密码不正确!");
        }
        // 生成token
        String token = JwtUtil.sign(username, userPassword);
        // 设置token缓存有效时间
        PasswordUtil.setSessionExpire(token,token);
        // 保存登录日志
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(username);
        loginLog.setSystemBrowserInfo();
        this.loginLogService.saveLoginLog(loginLog);
        return  ResultResponse.ok().put("token",token);
    }

    @PostMapping("regist")
    public ResultResponse regist(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) throws HsException {
        User user = userService.findByName(username);
        if (ObjectUtil.isNotNull(user)) {
            throw new HsException("该用户名已存在");
        }
        this.userService.regist(username, password);
        return  ResultResponse.ok();
    }

    @GetMapping("index/{username}")
    public ResultResponse index(@NotBlank(message = "{required}") @PathVariable String username) {
        // 更新登录时间
        this.userService.updateLoginTime(username);
        Map<String, Object> data = new HashMap<>();
        // 获取系统访问记录
        Long totalVisitCount = this.loginLogService.findTotalVisitCount();
        data.put("totalVisitCount", totalVisitCount);
        Long todayVisitCount = this.loginLogService.findTodayVisitCount();
        data.put("todayVisitCount", todayVisitCount);
        Long todayIp = this.loginLogService.findTodayIp();
        data.put("todayIp", todayIp);
        // 获取近期系统访问记录
        List<Map<String, Object>> lastSevenVisitCount = this.loginLogService.findLastSevenDaysVisitCount(null);
        data.put("lastSevenVisitCount", lastSevenVisitCount);
        User param = new User();
        param.setUsername(username);
        List<Map<String, Object>> lastSevenUserVisitCount = this.loginLogService.findLastSevenDaysVisitCount(param);
        data.put("lastSevenUserVisitCount", lastSevenUserVisitCount);
        return ResultResponse.ok().data(data);
    }
    /**
     * 退出登录
     * @return
     */
    @RequestMapping(value = "/logout")
    public ResultResponse logout(HttpServletRequest request, HttpServletResponse response) {
        //用户退出逻辑
        super.logout();
        //清空用户Token缓存
        PasswordUtil.delToken();
        return ResultResponse.ok().message("退出登录成功！");
    }

}
