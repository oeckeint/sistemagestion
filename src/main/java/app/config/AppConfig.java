package app.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = {"app", "controladores", "datos"})
public class AppConfig {

    @Bean
    public ViewResolver viewResolver(){
        return new InternalResourceViewResolver("/WEB-INF/view/", ".jsp");
    }
    
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver(){
        CommonsMultipartResolver cmr = new CommonsMultipartResolver();
        cmr.setDefaultEncoding("UTF-8");
        cmr.setMaxUploadSize(5242880);
        return cmr;
    }
    
    @Bean(destroyMethod = "close", name = "dataSource")
    public ComboPooledDataSource getDataSource() throws PropertyVetoException{
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.cj.jdbc.Driver");
        cpds.setJdbcUrl("jdbc:mysql://localhost:3306/sge?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        cpds.setUser("root");
        cpds.setPassword("admin");
        cpds.setMinPoolSize(5);
        cpds.setMaxPoolSize(20);
        cpds.setMaxIdleTime(30000);
        return cpds;
    }
    
    @Bean(name="sessionFactory")
    @Scope("singleton")
    public LocalSessionFactoryBean getSessionFactory(javax.sql.DataSource dataSource) throws PropertyVetoException{
        LocalSessionFactoryBean lsfb = new LocalSessionFactoryBean();
        lsfb.setDataSource(this.getDataSource());
        lsfb.setPackagesToScan("datos.entity");
        lsfb.setHibernateProperties(additionalProperties());
        return  lsfb;
    }
    
    public Properties additionalProperties(){
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.setProperty("hibernate.show_sql", "true");
        return  props;
    }
    
    @Bean(name="transactionManager")
    public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) throws PropertyVetoException{
        HibernateTransactionManager htm = new HibernateTransactionManager();
        htm.setSessionFactory(sessionFactory);
        return htm;
    }
    
}
