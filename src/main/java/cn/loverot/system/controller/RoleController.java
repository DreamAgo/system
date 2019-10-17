package cn.loverot.system.controller;


import cn.loverot.basic.utils.BasicUtil;
import cn.loverot.common.annotation.ControllerEndpoint;
import cn.loverot.common.entity.QueryRequest;
import cn.loverot.common.entity.ResultResponse;
import cn.loverot.common.exception.HsException;
import cn.loverot.system.entity.Role;
import cn.loverot.system.service.IRoleService;
import com.wuwenze.poi.ExcelKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * @author huise
 */
@Slf4j
@RestController
@RequestMapping("role")
public class RoleController extends BaseController {

    @Autowired
    private IRoleService roleService;

    @GetMapping
    public ResultResponse getAllRoles(Role role) {
        return  ResultResponse.build().ok().data(roleService.findRoles(role));
    }

    @GetMapping("list")
    @RequiresPermissions("role:view")
    public ResultResponse roleList(Role role, QueryRequest request) {
        Map<String, Object> dataTable = BasicUtil.getDataTable(this.roleService.findRoles(role, request));
        return  ResultResponse.build().ok().data(dataTable);
    }

    @PostMapping
    @RequiresPermissions("role:add")
    @ControllerEndpoint(operation = "新增角色", exceptionMessage = "新增角色失败")
    public ResultResponse addRole(@Valid Role role) {
        this.roleService.createRole(role);
        return ResultResponse.build().ok();
    }

    @GetMapping("delete/{roleIds}")
    @RequiresPermissions("role:delete")
    @ControllerEndpoint(operation = "删除角色", exceptionMessage = "删除角色失败")
    public ResultResponse deleteRoles(@NotBlank(message = "{required}") @PathVariable String roleIds) {
        this.roleService.deleteRoles(roleIds);
        return ResultResponse.build().ok();
    }

    @PostMapping("update")
    @RequiresPermissions("role:update")
    @ControllerEndpoint(operation = "修改角色", exceptionMessage = "修改角色失败")
    public ResultResponse updateRole(Role role) {
        this.roleService.updateRole(role);
        return ResultResponse.build().ok();
    }

    @GetMapping("excel")
    @RequiresPermissions("role:export")
    @ControllerEndpoint(exceptionMessage = "导出Excel失败")
    public void export(QueryRequest queryRequest, Role role, HttpServletResponse response) throws HsException {
        List<Role> roles = this.roleService.findRoles(role, queryRequest).getRecords();
        ExcelKit.$Export(Role.class, response).downXlsx(roles, false);
    }

}
