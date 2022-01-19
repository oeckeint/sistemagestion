package app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        UserBuilder users = User.withDefaultPasswordEncoder();
        auth.inMemoryAuthentication()
                .withUser(users.username("Eduardo").password("Com2Energy").roles("ADMIN"))
                .withUser(users.username("Jesus").password("123").roles("ADMIN", "EMPLOYEE"))
                .withUser(users.username("Mary").password("123").roles("EMPLOYEE"))
                .withUser(users.username("Susan").password("123").roles("ADMIN"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers(
                            "/",
                            "/clientes", 
                            "/clientes/detalles",
                            "/clientes/busqueda",
                            "/peajes/**",
                            "/facturas/**",
                            "/otrasfacturas/**").hasAnyRole("ADMIN", "EMPLOYEE")
                    .antMatchers(
                            "/clientes/formulario",
                            "/clientes/guardar",
                            "/clientes/editar",
                            "/clientes/eliminar",
                            "/procesar/**",
                            "/clasificar/**",
                            "/configuraciones/**",
                            "/ftp/**").hasAnyRole("ADMIN")
            .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/authenticateTheUser")
                    .permitAll()
            .and()
                .logout().permitAll();
    }
    
}
