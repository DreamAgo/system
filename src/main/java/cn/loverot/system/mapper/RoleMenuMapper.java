package cn.loverot.system.mapper;

import cn.loverot.system.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author huise
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    /**
     * 递归删除菜单/按钮
     *
     * @param menuId id
     */
    void deleteRoleMenus(String menuId);
}
