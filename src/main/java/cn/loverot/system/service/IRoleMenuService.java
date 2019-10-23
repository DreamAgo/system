package cn.loverot.system.service;

import cn.loverot.system.entity.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author huise
 */
public interface IRoleMenuService extends IService<RoleMenu> {

    /**
     * 通过角色 id 删除
     *
     * @param roleIds 角色 id
     */
    void deleteRoleMenusByRoleId(List<String> roleIds);

    /**
     * 通过菜单（按钮）id 删除
     *
     * @param menuIds 菜单（按钮）id
     */
    void deleteRoleMenusByMenuId(List<String> menuIds);

    /**
     * 递归删除菜单/按钮
     *
     * @param menuId id
     */
    void deleteRoleMenus(String menuId);
}
