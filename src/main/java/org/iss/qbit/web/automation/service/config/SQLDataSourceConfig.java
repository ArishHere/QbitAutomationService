package org.iss.qbit.web.automation.service.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.zaxxer.hikari.HikariDataSource;

@ConfigurationProperties(prefix = "com.iss.qbit.db.datasource", exceptionIfInvalid = true)
@Configuration
@lombok.Getter
@lombok.Setter
@Order(Integer.MIN_VALUE)
class SQLDataSourceConfig
{

	private String	username;
	private String	password;
	private String	url;
	private String	driverClassName;
	private String	poolName;
	private Integer	connectionTimeout;
	private Integer	maxLifetime;
	private Integer	maximumPoolSize;
	private Integer	minimumIdle;
	private Integer	idleTimeout;
	private String	connectionTestQuery;
	private String	connectionInitSql;

	@Bean
	public DataSource primaryDataSource()
	{
		HikariDataSource ds = new HikariDataSource();

		if(url==null || username==null || password==null || driverClassName==null)
			throw new RuntimeException("Fields marked with [*] cant be null.  MySQLDataSourceConfig[*username=" + username + ", *password=" + password + ", *url=" + url + ", *driverClassName=" + driverClassName + ", poolName=" + poolName + ", connectionTimeout=" + connectionTimeout + ", maxLifetime=" + maxLifetime + ", maximumPoolSize=" + maximumPoolSize + ", minimumIdle=" + minimumIdle + ", idleTimeout=" + idleTimeout + ", connectionTestQuery=" + connectionTestQuery + ", connectionInitSql=" + connectionInitSql + "]");
		ds.setJdbcUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setDriverClassName(driverClassName);
		if (poolName != null)ds.setPoolName(poolName);
		if (maximumPoolSize != null) ds.setMaximumPoolSize(maximumPoolSize);
		if (minimumIdle != null) ds.setMinimumIdle(minimumIdle);
		if (connectionTimeout != null) ds.setConnectionTimeout(connectionTimeout);
		if (idleTimeout != null) ds.setIdleTimeout(idleTimeout);
		if (connectionTestQuery != null) ds.setConnectionTestQuery(connectionTestQuery);
		if (connectionInitSql != null) ds.setConnectionInitSql(connectionInitSql);
		
		return ds;
	}

	@Override
	public String toString()
	{
		return "MySQLDataSourceConfig [username=" + username + ", password=" + password + ", url=" + url + ", driverClassName=" + driverClassName + ", poolName=" + poolName + ", connectionTimeout=" + connectionTimeout + ", maxLifetime=" + maxLifetime + ", maximumPoolSize=" + maximumPoolSize + ", minimumIdle=" + minimumIdle + ", idleTimeout=" + idleTimeout + ", connectionTestQuery=" + connectionTestQuery + ", connectionInitSql=" + connectionInitSql + "]";
	}
	
}