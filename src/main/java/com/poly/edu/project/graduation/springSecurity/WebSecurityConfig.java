package com.poly.edu.project.graduation.springSecurity;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import com.poly.edu.project.graduation.services.UserService;
import com.poly.edu.project.graduation.services.impl.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private DataSource dataSource;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}
	
	// Cấu hình xác thực người dùng
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().cors().disable();
		
		// Cấu hình các URL không yêu cầu xác thực (authentication)
		http.authorizeRequests().antMatchers("/", "/login", "/logout").permitAll();
		
		// Cấu hình các URL yêu cầu vai trò ROLE_USER hoặc ROLE_ADMIN để truy cập
		http.authorizeRequests().antMatchers("/userInfo").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')");
		
		// Cấu hình các URL chỉ dành cho ROLE_ADMIN
		http.authorizeRequests().antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')");
		
		// Cấu hình trang 403 khi truy cập bị từ chối
		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

		// Cấu hình trang đăng nhập và xử lý đăng nhập
		http.authorizeRequests().and().formLogin()
			.loginProcessingUrl("/j_spring_security_check")
			.loginPage("/login")
			.defaultSuccessUrl("/admin/manager_order_product")
			.failureUrl("/login?error=true")
			.usernameParameter("username")
			.passwordParameter("password");
		
		// Cấu hình trang đăng xuất
		http.authorizeRequests().and().logout()
			.logoutUrl("/logout")
			.logoutSuccessUrl("/login");
		
		// Cấu hình Remember Me để giữ phiên đăng nhập
		http.authorizeRequests().and().rememberMe()
			.tokenRepository(this.persistentTokenRepository())
			.tokenValiditySeconds(1 * 24 * 60 * 60); // 24 giờ
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
	    InMemoryTokenRepositoryImpl memory = new InMemoryTokenRepositoryImpl();
	    return memory;
	}
}
