package com.vote.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
/**
 * Configures the security for the application
 * 
 * @author XXX
 * @version 0.1.0
 *
 */
public class WebSecurityAppConfig extends WebSecurityConfigurerAdapter {
	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("foo").password("bar").roles("USER");
    }
	@Override
	
	
	protected void configure(HttpSecurity http) throws Exception {
		
		http
		.csrf().disable()
        .authorizeRequests()
            .antMatchers("/api/v1/moderators/*").hasRole("USER").and()
            .httpBasic();
	}

	

}