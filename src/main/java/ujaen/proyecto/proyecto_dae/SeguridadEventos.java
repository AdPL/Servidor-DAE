/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ujaen.proyecto.proyecto_dae;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
        auth.userDetailsService(servicioDatosSeguridadEventos);
    }
    
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.httpBasic();
        
        httpSecurity.authorizeRequests().antMatchers("/app/eventos/**").hasRole("USUARIO");
    }
}
