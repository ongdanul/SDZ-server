package com.elice.sdz.global.config;

import com.elice.sdz.global.jwt.*;
import com.elice.sdz.user.repository.RefreshRepository;
import com.elice.sdz.user.repository.UserRepository;
import com.elice.sdz.user.service.CustomOAuth2UserService;
import com.elice.sdz.user.service.CustomUserDetailsService;
import com.elice.sdz.user.service.ReissueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static com.elice.sdz.global.config.SecurityConstants.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final ReissueService reissueService;
    private final RefreshRepository refreshRepository;
    private final AuthenticationConfiguration configuration;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOauth2SuccessHandler customOauth2SuccessHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public BCryptPasswordEncoder cryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //예외 처리
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(customAccessDeniedHandler));

        //CSRF 보호 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        //CORS
        http.cors(cors -> cors.configurationSource(request -> {
            var config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:5173"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
            config.setAllowCredentials(true);
            config.setAllowedHeaders(List.of("Authorization"));
            return config;
        }));

        //접근 권한 설정
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/account/**", "/api/user/sign-up", "/api/check/**", "/oauth2/**").permitAll()
                        .requestMatchers("/api/categories/**", "/api/orders/**", "/api/order-item/**", "/api/products/**", "/api/deliveryAddress/**", "/api/user/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()/*hasRole("ADMIN")*/
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated())
                //로그인 테스트
                .formLogin(form -> form
                        .loginProcessingUrl("/api/user/loginProcess")
                        .permitAll())
                //LoginFilter 추가
                .addFilterBefore(new JWTFilter(jwtUtil, reissueService), CustomLoginFilter.class)
                .addFilterAt(
                        new CustomLoginFilter(jwtUtil, authenticationManager(configuration), userRepository,
                        refreshRepository, customAuthenticationFailureHandler),
                        UsernamePasswordAuthenticationFilter.class)
                //LogoutFilter 추가
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class)
                //소셜 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customOauth2SuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler));

        //세션 사용X
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
