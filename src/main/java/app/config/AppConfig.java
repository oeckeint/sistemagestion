package app.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import datos.helper.StringToLinkedHashMap;
import liquibase.integration.spring.SpringLiquibase;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.FormatterRegistry;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
@EnableJpaRepositories(basePackages = {"datos"})
@ComponentScan(basePackages = {"app", "controladores", "controladores.helper", "datos.dao.medidas", "controladores.otrosControladores", "datos", "datos.dao", "datos.service", "documentos.procesamiento"})
@PropertySources({@PropertySource("classpath:persistence-mysql.properties"), @PropertySource("classpath:/cfg/application.properties")})
public class AppConfig implements WebMvcConfigurer{

    @Autowired
    private Environment env;

    private Logger logger = Logger.getLogger(getClass().getName());

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/view/");
        internalResourceViewResolver.setSuffix(".jsp");
        internalResourceViewResolver.setContentType("text/html; charset=ISO-8859-1");
        return internalResourceViewResolver;
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
            cpds.setDriverClass(this.env.getProperty("spring.datasource.driver-class-name"));
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }

        cpds.setJdbcUrl(this.env.getProperty("spring.datasource.url"));
        cpds.setUser(this.env.getProperty("spring.datasource.username"));
        cpds.setPassword(this.env.getProperty("spring.datasource.password"));
        cpds.setInitialPoolSize(this.getIntProperty("connection.pool.initialPoolSize"));
        cpds.setMinPoolSize(this.getIntProperty("connection.pool.minPoolSize"));
        cpds.setMaxPoolSize(this.getIntProperty("connection.pool.maxPoolSize"));
        cpds.setMaxIdleTime(this.getIntProperty("connection.pool.maxIdleTime"));

        return cpds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(javax.sql.DataSource dataSource) throws PropertyVetoException {
        LocalSessionFactoryBean lsfb = new LocalSessionFactoryBean();
        lsfb.setDataSource(dataSource);
        //lsfb.setDataSource(this.securityDataSource());
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

    @Override
    public void addFormatters(FormatterRegistry registry){
        registry.addConverter(new StringToLinkedHashMap());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws ClassNotFoundException, PropertyVetoException {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
        emf.setJpaVendorAdapter(jpaAdapter());
        emf.setJpaProperties(jpaProperties());
        return emf;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    @Bean
    public JpaVendorAdapter jpaAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform(env.getProperty("hibernate.dialect"));
        adapter.setShowSql(env.getProperty("hibernate.show_sql", Boolean.class));
        return adapter;
    }

    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        return properties;
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource());
        liquibase.setChangeLog(env.getProperty("spring.liquibase.change-log"));
        return liquibase;
    }
    
}