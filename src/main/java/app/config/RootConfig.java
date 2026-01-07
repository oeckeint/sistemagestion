package app.config;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import utileria.spring.AppFeatureProperties;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = {"datos", "utileria"})
@EnableConfigurationProperties(AppFeatureProperties.class)
@EnableJpaRepositories(basePackages = {"datos", "core.repository"})
@EnableTransactionManagement
@PropertySources({
        @PropertySource("classpath:persistence-mysql.properties"),
        @PropertySource("classpath:/cfg/application.properties")
})
@RequiredArgsConstructor
public class RootConfig {

    private final Environment env;

    // DataSource
    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    // EntityManagerFactory
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(HikariDataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
        emf.setJpaVendorAdapter(jpaAdapter());
        emf.setJpaProperties(jpaProperties());
        emf.setDataSource(dataSource);
        return emf;
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
        properties.setProperty("hibernate.allow_update_outside_transaction", "true"); //Permite transacciones con Hibernate y JPA, aunque no es recomendable. Solución temporal para evitar errores en operaciones simples.
        return properties;
    }


    // Liquibase
    @Bean
    public SpringLiquibase liquibase(HikariDataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(env.getProperty("spring.liquibase.change-log"));
        return liquibase;
    }

    // TransactionManager
    @Bean
    public JpaTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public SessionFactory sessionFactory(@Qualifier("entityManagerFactory") EntityManagerFactory emf) {
        if (emf.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("No es una factory de hibernate");
        }
        return emf.unwrap(SessionFactory.class);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
