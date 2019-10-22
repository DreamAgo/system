package cn.loverot.system.aspect;

import cn.loverot.basic.utils.BasicUtil;
import cn.loverot.basic.utils.SpringUtil;
import cn.loverot.common.annotation.ControllerEndpoint;
import cn.loverot.common.exception.HsException;
import cn.loverot.system.service.ILogService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author huise
 */
@Aspect
@Component
public class ControllerEndpointAspect extends AspectSupport {

    @Autowired
    private ILogService logService;

    @Pointcut("@annotation( cn.loverot.common.annotation.ControllerEndpoint)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws HsException {
        Object result;
        Method targetMethod = resolveMethod(point);
        ControllerEndpoint annotation = targetMethod.getAnnotation(ControllerEndpoint.class);
        String operation = annotation.operation();
        long start = System.currentTimeMillis();
        try {
            result = point.proceed();
            if (StringUtils.isNotBlank(operation)) {
                HttpServletRequest request = SpringUtil.getRequest();
                logService.saveLog(point, targetMethod, request, operation, start);
            }
            return result;
        } catch (Throwable throwable) {
            String exceptionMessage = annotation.exceptionMessage();
            String message = throwable.getMessage();
            String error = StringUtils.isNotBlank(message)&& BasicUtil.containChinese(message) ? exceptionMessage + "，" + message : exceptionMessage;
            throw new HsException(error);
        }
    }
}



