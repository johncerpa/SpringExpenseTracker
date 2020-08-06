package com.myproject.expensetrackerapi;

import com.myproject.expensetrackerapi.filters.AuthFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class ExpenseTrackerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackerApiApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<AuthFilter> filterRegistrationBean() {
		FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
		AuthFilter authFilter = new AuthFilter();

		// endpoint is protected (user needs to provide JWT to verify login)
		registrationBean.setFilter(authFilter);
		registrationBean.addUrlPatterns("/api/categories/*");

		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> myCorsFilter() {
		FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		// Allow every origin (just for testing)
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");

		// Apply configuration to all paths
		source.registerCorsConfiguration("/**", config);
		registrationBean.setFilter(new CorsFilter(source));

		// First filter in filter chain
		registrationBean.setOrder(0);

		return registrationBean;
	}
}
