package com.nado.smartcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	SecurityFilterChain filter(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((request) -> request.requestMatchers("/**").permitAll()
			.requestMatchers("/css/**", "/js/**").permitAll()
			.requestMatchers("/api/check-login-status").permitAll()
			.requestMatchers("/oauth2/authorization/kakao").permitAll()
			.requestMatchers("/auth/kakao/**").permitAll()
			.anyRequest().authenticated()
		)
		.formLogin((form) -> form
		    .loginPage("/member/login")
		    .usernameParameter("username")
		    .passwordParameter("password")
		    .defaultSuccessUrl("/main", true)
		    .failureUrl("/member/login?error")
		    .permitAll()
			)
		.oauth2Login(oauth2 -> oauth2
                .loginPage("/member/login")
                .successHandler((request, response, authentication) -> {
                    if(authentication instanceof OAuth2AuthenticationToken token) {
                        response.sendRedirect("/auth/kakao/callback");
                    } else {
                        response.sendRedirect("/member/login");
                    }
                })
                .permitAll()
        )
		.csrf(csrf -> csrf.disable())
		.logout((logout) -> logout
			.logoutUrl("/logout")
			.logoutSuccessUrl("/main")
			.invalidateHttpSession(true)
			.deleteCookies("JSESSIONID")
			.permitAll()
		);
		
		return http.build();
	}
	
	
}
