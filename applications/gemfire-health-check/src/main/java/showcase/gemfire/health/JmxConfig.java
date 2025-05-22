package showcase.gemfire.health;

import lombok.SneakyThrows;
import org.apache.geode.management.DistributedSystemMXBean;
import org.apache.geode.management.MemberMXBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * JMX related configuration
 * @author gregory green
 */
@Configuration
public class JmxConfig {

    @Value("${jmx.service.url}")
    private String url;

    @Value("${jmx.service.username:}")
    private String userid;

    @Value("${jmx.service.password:}")
    private String password;

    @Value(("${gemfire.jmx.distributeSystem.mBean.name:GemFire:service=System,type=Distributed}"))
    private String distributeSystemMBeanName;

    @Value(("${gemfire.jmx.locator.mBean.name}"))
    private String locatorMBeanName;

    /**
     *
     * @param mbeanServerConnection the JMX connection
     * @return function to get mbean proxy from object
     */
    @Bean
    Function<ObjectName, MemberMXBean> toMemberMxBean(MBeanServerConnection mbeanServerConnection)
    {
        return objectName -> javax.management.JMX.newMBeanProxy(mbeanServerConnection, objectName, MemberMXBean.class);
    }

    @SneakyThrows
    @Bean
    MBeanServerConnectionFactoryBean jmxConnection()
    {
        var properties = new Properties();

        var factory = new MBeanServerConnectionFactoryBean();
        factory.setServiceUrl(url);
        factory.setEnvironmentMap(Map.of(JMXConnector.CREDENTIALS,
                new String[] { userid, password }));

        return factory;
    }

    @SneakyThrows
    @Bean
    MBeanProxyFactoryBean getDistributedSystemMBean(MBeanServerConnection server)
    {
        var factory = new MBeanProxyFactoryBean();
        factory.setObjectName(distributeSystemMBeanName);
        factory.setProxyInterface(DistributedSystemMXBean.class);
        factory.setServer(server);

        return factory;
    }

    @SneakyThrows
    @Bean
    MBeanProxyFactoryBean getLocatorMemberMXBean(MBeanServerConnection server)
    {
        var factory = new MBeanProxyFactoryBean();
        factory.setObjectName(locatorMBeanName);
        factory.setProxyInterface(MemberMXBean.class);
        factory.setServer(server);

        return factory;
    }
}
