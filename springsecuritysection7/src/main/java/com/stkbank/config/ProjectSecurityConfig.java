package com.stkbank.config;

import com.stkbank.exceptionhandling.CustomAccessDeniedHandler;
import com.stkbank.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

@Configuration
@Profile("!prod")
public class ProjectSecurityConfig {
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        /*Below makes sure to only accept HTTP traffic*/
//        http.sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true))
        /*below config is used to avoid common session fixation attacks, that occur via session ids
        * by default spring security handles session fixation attacks, by using changeSessionId() method
        * but there are also other configs that you can use like, migrateSession(), changeSessionId()
        * and also there is one moore method called none(), which you've to only use when you don't want any session fixation attack
        * prevention from springsecurity, means you've to implement your own session fixation attack prevention logic*/
        http.sessionManagement(session -> session.sessionFixation((sessionFixation -> sessionFixation.newSession())))
                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure())
                .csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                        .requestMatchers("/notices", "/contact", "/register", "/invalidSession").permitAll());
        http.formLogin(Customizer.withDefaults());
        /*Commented below cause, now we don't want default behavioru, we've our custom
         * response created in CustomBasicAuthenticationEntryPoint, when authentication fails*/
//        http.httpBasic(Customizer.withDefaults());
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        /*Added below, cause now we want to throw our modified custom exception message when any forbidden issues come.*/
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
