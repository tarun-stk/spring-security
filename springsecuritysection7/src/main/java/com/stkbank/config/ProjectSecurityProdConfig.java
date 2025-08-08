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

@Profile("prod")
@Configuration
public class ProjectSecurityProdConfig {
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        /*invalidSessionUrl("/invalidSession") -> if session became invalidated due to timeout or session id tampering, we can redirect user to
         * to a relogin by specifying a url, like above
         *.maximumSessions(1) -> we can allow maximum sessions that a user can created using this, if user tries to create more sessions than
         * specified in this, the older ones will be terminated to accomodate new sessions
         *  maxSessionsPreventsLogin(true) -> If user tries to create more number of sessions */
//        http.sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true))
//                http.requiresChannel(rcc -> rcc.anyRequest().requiresSecure())
                http.csrf(csrfConfig -> csrfConfig.disable())
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
