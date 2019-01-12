package com.github.chenhaiyangs.gateway.service.storage;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 配置数据源,使用阿里Druid数据源
 * @author chenhaiyang
 *
 */
@Configuration
@SuppressWarnings("all")
public class DataSourceConfiguration {
    /**
     * 连接地址
     */
	@Value("#{dataSourceGroup['mysqlurl']}")
	public String url;

    /**
     * 用户名
     */
	@Value("#{dataSourceGroup['mysqlusername']}")
	private String username;

    /**
     * 密码
     */
	@Value("#{dataSourceGroup['mysqlpassword']}")
	private String password;

    /**
     * 配置最大连接
     */
    @Value("#{dataSourceGroup['maxActive']}")
	private Integer maxActive;

    /**
     * 配置初始连接
     */
    @Value("#{dataSourceGroup['initialSize']}")
    private Integer initialSize;

    /**
     * 配置最小连接
     */
    @Value("#{dataSourceGroup['minIdle']}")
    private Integer minIdle;

    /**
     * 连接等待超时时间
     */
    @Value("#{dataSourceGroup['maxWait']}")
    private Integer maxWait;

    /**
     * 连接等待超时时间
     */
    @Value("#{dataSourceGroup['timeBetweenEvictionRunsMillis']}")
    private Integer timeBetweenEvictionRunsMillis;

    /**
     * 连接等待超时时间
     */
    @Value("#{dataSourceGroup['minEvictableIdleTimeMillis']}")
    private Integer minEvictableIdleTimeMillis;

	/**
	 * 配置Druid数据源
	 * @return {@link DataSource}
	 * @throws SQLException sqlExecption
	 */
	@Bean(name="dataSource")
	public DataSource dataSource() throws SQLException{
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(username);
        dataSource.setPassword(password);
        //配置最大连接
        dataSource.setMaxActive(maxActive);
        //配置初始连接
        dataSource.setInitialSize(initialSize);
        //配置最小连接
        dataSource.setMinIdle(minIdle);
        //连接等待超时时间
        dataSource.setMaxWait(maxWait);
        //间隔多久进行检测,关闭空闲连接
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        //一个连接最小生存时间
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        //连接等待超时时间 单位为毫秒 缺省启用公平锁，
        //并发效率会有所下降， 如果需要可以通过配置useUnfairLock属性为true使用非公平锁
        dataSource.setUseUnfairLock(true);
        //用来检测是否有效的sql
        dataSource.setValidationQuery("select 'x' from dual");
        dataSource.setTestWhileIdle(true);
        //申请连接时执行validationQuery检测连接是否有效，配置为true会降低性能
        dataSource.setTestOnBorrow(false);
        //归还连接时执行validationQuery检测连接是否有效，配置为true会降低性能
        dataSource.setTestOnReturn(false);
        return dataSource;
	}
}
