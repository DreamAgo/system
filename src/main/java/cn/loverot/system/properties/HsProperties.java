package cn.loverot.system.properties;

import cn.loverot.basic.autoconfigure.BasicProperties;
import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * @author huise
 */
@Data
@ConfigurationProperties(prefix = HsProperties.SHIRO_PREFIX)
public class HsProperties {
    public final static String SHIRO_PREFIX = "hs.system";
    private ShiroProperties shiro = new ShiroProperties();
    private boolean autoOpenBrowser = true;
    private SwaggerProperties swagger = new SwaggerProperties();
    @Data
    public static class ShiroProperties {

        private long sessionTimeout=3600;
        private int cookieTimeout=86400;
        private String anonUrl="/img/**,/layui/**,/json/**,/code,/regist";
        private String loginUrl="/login";
        private String successUrl="/index";
        private String logoutUrl="/logout";
        private String unauthorizedUrl="/unauthorized";
    }
    @Data
    public static class SwaggerProperties {
        private String basePackage="cn.loverot";
        private String title="Shiro API";
        private String description="Shiro API Document";
        private String version="2.0";
        private String author="huise";
        private String url="blog.loverot.cn";
        private String email="995959152@qq.com";
        private String license="Apache 2.0";
        private String licenseUrl="https://www.apache.org/licenses/LICENSE-2.0.html";
    }
}
