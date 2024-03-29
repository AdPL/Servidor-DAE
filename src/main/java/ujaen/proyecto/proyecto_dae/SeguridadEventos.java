/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ujaen.proyecto.proyecto_dae;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author adpl
 */
@Configuration
public class SeguridadEventos extends WebSecurityConfigurerAdapter {
    
    @Autowired
    ServicioDatosSeguridadEventos servicioDatosSeguridadEventos;
    
    @Override 
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(servicioDatosSeguridadEventos).passwordEncoder(new BCryptPasswordEncoder());
    }
    
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.httpBasic();
        
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/app/usuarios/{nombre}").hasRole("USUARIO");
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/app/usuarios/{nombre}/*").hasRole("USUARIO");
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/app/eventos/**").hasRole("USUARIO");
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.DELETE, "/app/eventos/**").hasRole("USUARIO");
    }
}
