package cn.loverot.system.service;


import cn.loverot.common.entity.QueryRequest;
import cn.loverot.system.constant.Const;
import cn.loverot.system.entity.SystemLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author huise
 */
public interface ILogService extends IService<SystemLog> {

    /**
     * 查询操作日志分页
     *
     * @param systemLog 日志
     * @param request   QueryRequest
     * @return IPage<SystemLog>
     */
    IPage<SystemLog> findLogs(SystemLog systemLog, QueryRequest request);

    /**
     * 删除操作日志
     *
     * @param logIds 日志 ID集合
     */
    void deleteLogs(String[] logIds);

    /**
     * 异步保存操作日志
     *
     * @param point     切点
     * @param method    Method
     * @param request   HttpServletRequest
     * @param operation 操作内容
     * @param start     开始时间
     */
    @Async(Const.ASYNC_POOL)
    void saveLog(ProceedingJoinPoint point, Method method, HttpServletRequest request, String operation, long start);
}
