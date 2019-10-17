package cn.loverot.system.controller;

import cn.hutool.core.date.DateUtil;
import cn.loverot.basic.utils.BasicUtil;
import cn.loverot.system.authentication.ShiroHelper;
import cn.loverot.system.entity.User;
import cn.loverot.system.service.IUserService;
import cn.loverot.system.utils.SystemUtil;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.ExpiredSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author huise
 */
@Controller("systemView")
public class ViewController extends BaseController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ShiroHelper shiroHelper;

    @GetMapping("login")
    @ResponseBody
    public Object login(HttpServletRequest request) {
        if (BasicUtil.isAjaxRequest()) {
            throw new ExpiredSessionException();
        } else {
            ModelAndView mav = new ModelAndView();
            mav.setViewName(SystemUtil.view("login"));
            return mav;
        }
    }

    @GetMapping("unauthorized")
    public String unauthorized() {
        return SystemUtil.view("error/403");
    }


    @GetMapping("/")
    public String redirectIndex() {
        return "redirect:/index";
    }

    @GetMapping("index")
    public String index(Model model) {
        AuthorizationInfo authorizationInfo = shiroHelper.getCurrentuserAuthorizationInfo();
        User user = super.getCurrentUser();
        User currentUserDetail = userService.findByName(user.getUsername());
        currentUserDetail.setPassword("It's a secret");
        model.addAttribute("user", currentUserDetail);
        model.addAttribute("permissions", authorizationInfo.getStringPermissions());
        model.addAttribute("roles", authorizationInfo.getRoles());
        return "index";
    }

    @GetMapping("layout")
    public String layout() {
        return SystemUtil.view("layout");
    }

    @GetMapping("password/update")
    public String passwordUpdate() {
        return SystemUtil.view("system/user/passwordUpdate");
    }

    @GetMapping("user/profile")
    public String userProfile() {
        return SystemUtil.view("system/user/userProfile");
    }

    @GetMapping("user/avatar")
    public String userAvatar() {
        return SystemUtil.view("system/user/avatar");
    }

    @GetMapping("user/profile/update")
    public String profileUpdate() {
        return SystemUtil.view("system/user/profileUpdate");
    }

    @GetMapping("system/user")
    @RequiresPermissions("user:view")
    public String systemUser() {
        return SystemUtil.view("system/user/user");
    }

    @GetMapping("system/user/add")
    @RequiresPermissions("user:add")
    public String systemUserAdd() {
        return SystemUtil.view("system/user/userAdd");
    }

    @GetMapping("system/user/detail/{username}")
    @RequiresPermissions("user:view")
    public String systemUserDetail(@PathVariable String username, Model model) {
        resolveUserModel(username, model, true);
        return SystemUtil.view("system/user/userDetail");
    }

    @GetMapping("system/user/update/{username}")
    @RequiresPermissions("user:update")
    public String systemUserUpdate(@PathVariable String username, Model model) {
        resolveUserModel(username, model, false);
        return SystemUtil.view("system/user/userUpdate");
    }

    @GetMapping("system/role")
    @RequiresPermissions("role:view")
    public String systemRole() {
        return SystemUtil.view("system/role/role");
    }

    @GetMapping("system/menu")
    @RequiresPermissions("menu:view")
    public String systemMenu() {
        return SystemUtil.view("system/menu/menu");
    }

    @GetMapping("system/dept")
    @RequiresPermissions("dept:view")
    public String systemDept() {
        return SystemUtil.view("system/dept/dept");
    }

    @RequestMapping( "index")
    public String pageIndex() {
        return SystemUtil.view("index");
    }

    @GetMapping("404")
    public String error404() {
        return SystemUtil.view("error/404");
    }

    @GetMapping("403")
    public String error403() {
        return SystemUtil.view("error/403");
    }

    @GetMapping("500")
    public String error500() {
        return SystemUtil.view("error/500");
    }

    private void resolveUserModel(String username, Model model, Boolean transform) {
        User user = userService.findByName(username);
        model.addAttribute("user", user);
        if (transform) {
            String ssex = user.getSex();
            if (User.SEX_MALE.equals(ssex)) {
                user.setSex("男");
            } else if (User.SEX_FEMALE.equals(ssex)) {
                user.setSex("女");
            } else {
                user.setSex("保密");
            }
        }
        if (user.getLastLoginTime() != null) {
            model.addAttribute("lastLoginTime", DateUtil.formatDateTime(user.getLastLoginTime()));
        }
    }
}
