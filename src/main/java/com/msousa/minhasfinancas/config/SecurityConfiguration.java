package com.msousa.minhasfinancas.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.msousa.minhasfinancas.api.JwtTokenFilter;
import com.msousa.minhasfinancas.service.JwtService;
import com.msousa.minhasfinancas.service.impl.SecurityUserDetailsService;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration{
	
	@Autowired
	private SecurityUserDetailsService userDetailsService;
	@Autowired
	private JwtService jwtService;

	
	@Bean
	@Autowired    
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
	@Bean
	public JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter(jwtService, userDetailsService);
	}
	@Bean
    AuthenticationManager authenticationManager(AuthenticationManagerBuilder builder) throws Exception {
        return builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder()).and().build();
    }
	 @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
				
	 	http.csrf().disable().exceptionHandling().authenticationEntryPoint((AuthenticationEntryPoint) jwtService).and()
	 	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeHttpRequests().
		requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
         // http.csrf().disable().authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                 .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
 			.requestMatchers(HttpMethod.POST, "/api/usuarios/autenticar").permitAll()
	 			.requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                 .anyRequest().authenticated().and()
	 		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	 	.and()
 		.addFilterBefore( jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class )
	 		;
        
			 return http.build();
	}
	
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter(){
		
		List<String> all = Arrays.asList("*");
		
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedMethods(all);
		config.setAllowedOrigins(all);
		config.setAllowedHeaders(all);
		config.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		
		CorsFilter corFilter = new CorsFilter(source);
		
		FilterRegistrationBean<CorsFilter> filter = 
				new FilterRegistrationBean<CorsFilter>(corFilter);
		filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
		return filter;
	}
	
	
	
	
}
