
package org.iss.qbit.web.automation.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.iss.qbit.web.commons.auth.MyCustomAuthenticationEntryPoint;
import org.iss.qbit.web.commons.utils.CustomServerInfo;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableRedisHttpSession
@EnableDiscoveryClient
@RestController
@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan({ "org.iss.qbit" })
public class AutomationServiceApplication extends SpringBootServletInitializer
{

	//	private static final Logger	log	= LoggerFactory.getLogger(AutomationServiceApplication.class);
	protected static Context ctx;

	static
	{
		try
		{
			ctx = new InitialContext();
			ctx = (Context) ctx.lookup("java:comp/env");
			//			String log4jLocation = (String) ctx.lookup("RecorderFactory.configuration");//AutomationUI-WebService
			//			//			String log4jLocation = "http://vpna-qat-rep001.ad-dev.issgovernance.com/repository/LOG4J/automationui-log4j.xml";
			//			System.out.println("log4jLocation:" + log4jLocation);
		}
		catch (NoInitialContextException e)
		{
			ctx = null;
		}
		catch (NamingException e)
		{
			e.printStackTrace();
		}
	}

	static Properties getProperties(boolean embeded) throws MalformedURLException, IOException, NamingException
	{
		Properties props = new Properties();
		//		props.load(new URL((String) ctx.lookup("LDAP.server")).openStream());
		//				props.load(new URL("http://vpna-qat-rep001.ad-dev.issgovernance.com/repository/QBIT/automationUI.properties").openStream());
		if (ctx != null)
		{
			props.put("spring.config.location", (String) ctx.lookup("app.properties"));
			props.put("logging.config", (String) ctx.lookup("log4j.url"));
		}
		props.putAll(CustomServerInfo.getDefaultProperties(embeded));
		System.out.println(props);
		return props;
	}

	public static void main(String[] args)
	{
		try
		{
			SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(applicationClass);
			springApplicationBuilder.sources(applicationClass).properties(getProperties(true)).run(args);
			// SpringApplication.run(applicationClass, args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{

		/*
		 * try
		 * {
		 * // Object o = FieldUtils.readField(ctx, "context", true);
		 * // StandardContext sCtx = (StandardContext) FieldUtils.readField(o, "context", true);
		 * Container container = ctx.;
		 * Container c = container.getParent();
		 * while (c != null && !(c instanceof StandardEngine))
		 * {
		 * c = c.getParent();
		 * }
		 * if (c != null)
		 * {
		 * StandardEngine engine = (StandardEngine) c;
		 * for (Connector connector : engine.getService().findConnectors())
		 * {
		 * // Get port for each connector. Store it in the ServletContext or whatever
		 * System.out.println(connector.getPort());
		 * }
		 * }
		 * }
		 * catch (Exception e)
		 * {
		 * e.printStackTrace();
		 * }
		 * try
		 * {
		 * // Object o = FieldUtils.readField(ctx, "context", true);
		 * // StandardContext sCtx = (StandardContext) FieldUtils.readField(o, "context", true);
		 * Container container = (Container) ctx;
		 * Container c = container.getParent();
		 * while (c != null && !(c instanceof StandardEngine))
		 * {
		 * c = c.getParent();
		 * }
		 * if (c != null)
		 * {
		 * StandardEngine engine = (StandardEngine) c;
		 * for (Connector connector : engine.getService().findConnectors())
		 * {
		 * // Get port for each connector. Store it in the ServletContext or whatever
		 * System.out.println(connector.getPort());
		 * }
		 * }
		 * }
		 * catch (Exception e)
		 * {
		 * e.printStackTrace();
		 * }
		 */
		try
		{
			return application.sources(applicationClass).properties(getProperties(false));
		}
		catch (IOException | NamingException e)
		{
			// e.printStackTrace();
			throw new RuntimeException("Failed to load spring app", e);
		}

	}

	private static Class<AutomationServiceApplication> applicationClass = AutomationServiceApplication.class;

	/*
	 * @Bean
	 * public DataSource dataSource()
	 * {
	 * JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
	 * DataSource dataSource = dataSourceLookup.getDataSource("java:comp/env/jdbc/MySQLHikari");
	 * return dataSource;
	 * }
	 */

	//	@Bean
	//	public ControllerClassNameHandlerMapping controllerClassNameHandlerMapping()
	//	{
	////		ControllerClassNameHandlerMapping url = new ControllerClassNameHandlerMapping(){
	////			protected String[] generatePathMappings(Class<?> beanClass) {
	////				String[] a = super.generatePathMappings(beanClass);
	////				for (int i = 0; i < a.length; i++) {
	////					a[i]=a[i].replaceAll("\\/\\*$", "/**");
	////				}
	////				return a;
	////			}
	////		};
	//		ControllerClassNameHandlerMapping url = new ControllerClassNameHandlerMapping();
	//		url.setBasePackage(this.getClass().getPackage().getName());
	//		url.setIncludeAnnotatedControllers(true);
	//		return url;
	//	}

	//	@Bean
	//	public ControllerClassNameHandlerMapping controllerClassNameHandlerMapping()
	//	{
	////		ControllerClassNameHandlerMapping url = new ControllerClassNameHandlerMapping(){
	////			protected String[] generatePathMappings(Class<?> beanClass) {
	////				String[] a = super.generatePathMappings(beanClass);
	////				for (int i = 0; i < a.length; i++) {
	////					a[i]=a[i].replaceAll("\\/\\*$", "/**");
	////				}
	////				return a;
	////			}
	////		};
	//		ControllerClassNameHandlerMapping url = new ControllerClassNameHandlerMapping();
	//		url.setBasePackage(this.getClass().getPackage().getName());
	//		url.setIncludeAnnotatedControllers(true);
	//		return url;
	//	}

	//	@Bean
	//	public HttpSessionStrategy customCookie()
	//	{
	//		MyCookieHttpSessionStrategy.setCookiePath("/qbit/");
	//		MyCookieHttpSessionStrategy c = new MyCookieHttpSessionStrategy();
	//		return c;
	//	}

	/*
	 * private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	 * @Autowired
	 * public void customCookie(EurekaInstanceConfigBean eurekaInstanceConfigBean)
	 * {
	 * Map<String, String> metadataMap = eurekaInstanceConfigBean.getMetadataMap();
	 * System.out.println("Before : " + metadataMap);
	 * metadataMap.put("Current", dateFormat.format(new Date()));
	 * System.out.println("After : " + metadataMap);
	 * eurekaInstanceConfigBean.setMetadataMap(metadataMap);
	 * }
	 */

	@RequestMapping("/user")
	@ResponseBody
	public Principal user(Principal user)
	{
		return user;
	}

	@RequestMapping(value = "/error/codes/{num}")
	public void returnErrorCodes(@PathVariable Integer num, HttpServletResponse res) throws IOException
	{
		System.out.println("================snum : " + num);
		if (num != null)
		{
			res.setStatus(num);
		}
	}

	/*
	 * ***********************************************************************************
	 * Login Config
	 * *************************************************************
	 * *********************
	 */
	@Configuration
	@Order(-10)
	@EnableWebSecurity
	//	@ConfigurationProperties(prefix = "com.iss.qbit.automationui.ldap.contextSource")
	protected static class LoginConfig extends WebSecurityConfigurerAdapter implements EnvironmentAware
	{

		private static String		loginPage;
		private static final String	prefix	= "com.iss.qbit.common.";

		@Override
		public void setEnvironment(Environment environment)
		{
			loginPage = environment.getProperty(prefix + "login.page");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception
		{
			/*
			 * SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
			 * handler.setUseReferer(true);
			 * handler.setTargetUrlParameter("r");
			 */

			MyCustomAuthenticationEntryPoint customAuthEntry = new MyCustomAuthenticationEntryPoint(loginPage, true, "r");
			//			customAuthEntry.setLoginPageUrl(loginPage);
			//			customAuthEntry.setReturnParameterEnabled(true);
			//			customAuthEntry.setReturnParameterName("r");
			// @formatter:off
			http
	            .logout()
	            .logoutUrl("/logout").permitAll()
	            .logoutSuccessUrl(loginPage+"?logout=success")
			.and()
				.authorizeRequests()
				.anyRequest()
				.authenticated()
			.and()
				.exceptionHandling()
				.authenticationEntryPoint(customAuthEntry)
				.and().csrf().disable()
				.headers()
				.contentTypeOptions().disable()
				;
			// @formatter:on
		}

	}

	//	@Component
	//	@Order(Ordered.HIGHEST_PRECEDENCE)
	//	static class CorsFilter implements Filter
	//	{
	//
	//		@Override
	//		public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
	//		{
	//			HttpServletResponse response = (HttpServletResponse) res;
	//			HttpServletRequest request = (HttpServletRequest) req;
	//			response.setHeader("Access-Control-Allow-Origin", "*");
	//			response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
	//			response.setHeader("Access-Control-Max-Age", "3600");
	//			response.setHeader("Access-Control-Allow-Headers", "x-auth-token, x-requested-with");
	//			if (request.getMethod().equals("OPTIONS"))
	//			{
	//				chain.doFilter(req, res);
	//			}
	//			else
	//			{
	//			}
	//		}
	//
	//		@Override
	//		public void init(FilterConfig filterConfig)
	//		{
	//		}
	//
	//		@Override
	//		public void destroy()
	//		{
	//		}
	//	}
}
