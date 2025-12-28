package app.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.util.StreamUtils;
import utileria.spring.AppFeatureProperties;
import java.beans.PropertyVetoException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;

import com.zaxxer.hikari.HikariDataSource;
import datos.helper.StringToLinkedHashMap;
import liquibase.integration.spring.SpringLiquibase;
import org.hibernate.SessionFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.FormatterRegistry;
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

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebMvc
@EnableConfigurationProperties(AppFeatureProperties.class)
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"datos"})
@ComponentScan(basePackages = {"controladores", "datos", "utileria"})
@PropertySources({@PropertySource("classpath:persistence-mysql.properties"), @PropertySource("classpath:/cfg/application.properties")})
public class AppConfig implements WebMvcConfigurer{

    private final Environment env;

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

    /**
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
     */

    @Bean
    public SessionFactory sessionFactory(EntityManagerFactory emf) {
        if (emf.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("No es una factory de hibernate");
        }
        return emf.unwrap(SessionFactory.class);
    }

    public Properties getHibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        props.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        return props;
    }

    /**
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) throws PropertyVetoException {
        HibernateTransactionManager htm = new HibernateTransactionManager();
        htm.setSessionFactory(sessionFactory);
        return htm;
    }
    */

    @Bean
    @Primary
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }


    private int getIntProperty(String propName) {
        return Integer.parseInt(Objects.requireNonNull(env.getProperty(propName)));
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
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws ClassNotFoundException, PropertyVetoException {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
        emf.setJpaVendorAdapter(jpaAdapter());
        emf.setJpaProperties(jpaProperties());
        return emf;
    }

    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource() {
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
        adapter.setShowSql(Boolean.TRUE.equals(env.getProperty("hibernate.show_sql", Boolean.class)));
        return adapter;
    }

    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        properties.setProperty("hibernate.current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext");
        properties.setProperty("hibernate.transaction.coordinator_class", "jdbc");
        properties.setProperty("hibernate.current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext");
        properties.setProperty("hibernate.allow_update_outside_transaction", "true");
        return properties;
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource());
        liquibase.setChangeLog(env.getProperty("spring.liquibase.change-log"));
        return liquibase;
    }

    static {
        try {
            ClassPathResource resource = new ClassPathResource("banner.txt");
            if (resource.exists()) {
                String banner = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                // Usamos System.out para que salga limpio o log.info para que use el formato de logback
                System.out.println(banner);
            }
        } catch (Exception e) {
            log.error("No se pudo cargar el banner...");
        }

        // deletes any existing handlers attached to j.u.l root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        // redirects jul (java.util.logging) to SLF4J
        SLF4JBridgeHandler.install();
    }

}