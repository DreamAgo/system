package cn.loverot.system.filter;

import cn.loverot.basic.utils.BasicUtil;
import cn.loverot.common.entity.ResultResponse;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @Author: 铭飞开源团队--huise
 * @Date: 2019/10/18 20:54
 */
public class ShiroLoginFilter extends UserFilter {

    /**
     * 在访问controller前判断是否登录，返回json，不进行重定向。
     * @param request
     * @param response
     * @return true-继续往下执行，false-该filter过滤器已经处理，不继续执行其他过滤器
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (BasicUtil.isAjaxRequest()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            ResultResponse message = ResultResponse.build().code(HttpStatus.FORBIDDEN).message("登录认证失效，请重新登录!");
            response.getWriter().write(JSONObject.toJSON(message).toString());
            return false;
        }
        //普通请求就内部处理
        return super.onAccessDenied(request,response);
    }
}
