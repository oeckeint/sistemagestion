package app.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = {"app", "controladores", "datos"})
@PropertySource("classpath:persistence-mysql.properties")
public class AppConfig implements WebMvcConfigurer{

    @Autowired
    private Environment env;

    private Logger logger = Logger.getLogger(getClass().getName());

    @Bean
    public ViewResolver viewResolver() {
        return new InternalResourceViewResolver("/WEB-INF/view/", ".jsp");
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver cmr = new CommonsMultipartResolver();
        cmr.setDefaultEncoding("UTF-8");
        cmr.setMaxUploadSize(52428800);
        return cmr;
    }

    @Bean(destroyMethod = "")
    public DataSource securityDataSource() throws PropertyVetoException {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        
        try {
            cpds.setDriverClass(this.env.getProperty("jdbc.driver"));
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }

        cpds.setJdbcUrl(this.env.getProperty("jdbc.url"));
        cpds.setUser(this.env.getProperty("jdbc.user"));
        cpds.setPassword(this.env.getProperty("jdbc.password"));
        cpds.setInitialPoolSize(this.getIntProperty("connection.pool.initialPoolSize"));
        cpds.setMinPoolSize(this.getIntProperty("connection.pool.minPoolSize"));
        cpds.setMaxPoolSize(this.getIntProperty("connection.pool.maxPoolSize"));
        cpds.setMaxIdleTime(this.getIntProperty("connection.pool.maxIdleTime"));
        
        logger.log(Level.INFO, ">>> jdbc.url={0}", this.env.getProperty("jdbc.url"));
        logger.log(Level.INFO, ">>> jdbc.user={0}", this.env.getProperty("jdbc.user"));

        return cpds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(javax.sql.DataSource dataSource) throws PropertyVetoException {
        LocalSessionFactoryBean lsfb = new LocalSessionFactoryBean();
        lsfb.setDataSource(this.securityDataSource());
        lsfb.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
        lsfb.setHibernateProperties(getHibernateProperties());
        return lsfb;
    }

    public Properties getHibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        props.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        return props;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) throws PropertyVetoException {
        HibernateTransactionManager htm = new HibernateTransactionManager();
        htm.setSessionFactory(sessionFactory);
        return htm;
    }

    private int getIntProperty(String propName) {
        return Integer.parseInt(env.getProperty(propName));
    }
    
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }
    
}