package com.tcube.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.web.AuthenticationEntryPoint;
/**
 * This is a web security setting class instance
 *
 */
@Configuration
@EnableWebSecurity
public class securityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthenticationEntryPoint authEntryPoint;

	/**
	 * This method is to define the configuration for all api related url files
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
		
//		.authorizeRequests().antMatchers("/api/**").hasAnyRole("USER").anyRequest().fullyAuthenticated().and()
//		.httpBasic().authenticationEntryPoint(authEntryPoint);
		
        .authorizeRequests()
        .antMatchers("/api/**").hasRole("USER")
        .and().httpBasic().realmName("tcubeweb").authenticationEntryPoint(authEntryPoint)
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//We don't need session.
		
//        .authorizeRequests().anyRequest().authenticated()
//        .and().httpBasic();
	}
	

	/**
	 * This method is to define the global configuration for all api related url files and 
	 * authenticate the url endpoint with api credentials to check the validity from authorized source
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		PropertiesConfig config = PropertiesConfig.getInstance();
		auth.inMemoryAuthentication().withUser(config.getRestApiUsername())
				.password("{noop}" + config.getRestApiPassword()).roles("USER");
//		UserBuilder users = User.withDefaultPasswordEncoder();		
//		auth.inMemoryAuthentication()
//		.withUser(users.username("tcube-web-admin").password("d[7%G+N66u:hXxn").roles("USER"));
	}

	/**
	 * This method is to configure static assets folder in the web app
	 */
	@Override
	public void configure(WebSecurity web) {
	}
}
