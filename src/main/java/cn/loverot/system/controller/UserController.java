package cn.loverot.system.controller;

import cn.hutool.crypto.SecureUtil;
import cn.loverot.common.annotation.ControllerEndpoint;
import cn.loverot.common.entity.QueryRequest;
import cn.loverot.common.entity.ResultResponse;
import cn.loverot.common.exception.HsException;
import cn.loverot.system.auth.ShiroRealm;
import cn.loverot.system.entity.User;
import cn.loverot.system.service.IUserService;
import cn.loverot.system.utils.SystemUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.wuwenze.poi.ExcelKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

import static cn.loverot.basic.utils.BasicUtil.getDataTable;

/**
 * @author huise
 */
@Slf4j
@Validated
@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ShiroRealm shiroRealm;

    @GetMapping("info")
    public ResultResponse getUser() {
        User currentUser = getCurrentUser();
        User userDetail = this.userService.findUserDetail(currentUser.getUsername());
        return ResultResponse.ok().data(userDetail);
    }

    @GetMapping("check/{username}")
    public boolean checkUserName(@NotBlank(message = "{required}") @PathVariable String username, String userId) {
        return this.userService.findByName(username) == null || StringUtils.isNotBlank(userId);
    }

    @GetMapping("list")
    @RequiresPermissions("user:view")
    public ResultResponse userList(User user, QueryRequest request) {
        Map<String, Object> dataTable = getDataTable(this.userService.findUserDetail(user, request));
        return ResultResponse.ok().data(dataTable);
    }

    @PostMapping
    @RequiresPermissions("user:add")
    @ControllerEndpoint(operation = "新增用户", exceptionMessage = "新增用户失败")
    public ResultResponse addUser(@Valid User user) {
        this.userService.createUser(user);
        return ResultResponse.ok();
    }

    @GetMapping("delete/{userIds}")
    @RequiresPermissions("user:delete")
    @ControllerEndpoint(operation = "删除用户", exceptionMessage = "删除用户失败")
    public ResultResponse deleteUsers(@NotBlank(message = "{required}") @PathVariable String userIds) {
        String[] ids = userIds.split(StringPool.COMMA);
        this.userService.deleteUsers(ids);
        return ResultResponse.ok();
    }

    @PostMapping("update")
    @RequiresPermissions("user:update")
    @ControllerEndpoint(operation = "修改用户", exceptionMessage = "修改用户失败")
    public ResultResponse updateUser(@Valid User user) {
        if (user.getId() == null) {
            throw new HsException("用户ID为空");
        }
        this.userService.updateUser(user);
        if (StringUtils.equalsIgnoreCase(getCurrentUser().getUsername(), user.getUsername())) {
            shiroRealm.clearCache();
        }
        return ResultResponse.ok();
    }

    @PostMapping("password/reset/{usernames}")
    @RequiresPermissions("user:password:reset")
    @ControllerEndpoint(exceptionMessage = "重置用户密码失败")
    public ResultResponse resetPassword(@NotBlank(message = "{required}") @PathVariable String usernames) {
        String[] usernameArr = usernames.split(StringPool.COMMA);
        this.userService.resetPassword(usernameArr);
        return ResultResponse.ok();
    }

    @PostMapping("password/update")
    @ControllerEndpoint(exceptionMessage = "修改密码失败")
    public ResultResponse updatePassword(
            @NotBlank(message = "{required}") String oldPassword,
            @NotBlank(message = "{required}") String newPassword) {
        User user = getCurrentUser();
        if (!StringUtils.equals(user.getPassword(), SecureUtil.sha1( oldPassword))) {
            throw new HsException("原密码不正确");
        }
        userService.updatePassword(user.getUsername(), newPassword);
        return ResultResponse.ok();
    }

    @GetMapping("avatar/{image}")
    @ControllerEndpoint(exceptionMessage = "修改头像失败")
    public ResultResponse updateAvatar(@NotBlank(message = "{required}") @PathVariable String image) {
        User user = getCurrentUser();
        this.userService.updateAvatar(user.getUsername(), image);
        return ResultResponse.ok();
    }

    @PostMapping("theme/update")
    @ControllerEndpoint(exceptionMessage = "修改系统配置失败")
    public ResultResponse updateTheme(String theme, String isTab) {
        User user = getCurrentUser();
        this.userService.updateTheme(user.getUsername(), theme, isTab);
        return ResultResponse.ok();
    }

    @PostMapping("profile/update")
    @ControllerEndpoint(exceptionMessage = "修改个人信息失败")
    public ResultResponse updateProfile(User user) throws HsException {
        User currentUser = getCurrentUser();
        user.setId(currentUser.getId());
        this.userService.updateProfile(user);
        return ResultResponse.ok();
    }

    @GetMapping("excel")
    @RequiresPermissions("user:export")
    @ControllerEndpoint(exceptionMessage = "导出Excel失败")
    public void export(QueryRequest queryRequest, User user, HttpServletResponse response) {
        List<User> users = this.userService.findUserDetail(user, queryRequest).getRecords();
        ExcelKit.$Export(User.class, response).downXlsx(users, false);
    }
}
