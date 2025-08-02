package com.stkbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectSecurityConfig {

    /*If you do not declare below bean, when working with spring security
    * by default all the apis of the app will be authenticated as mentioned in
    * SpringBootWebSecurityConfiguration class, having method defaultSecurityFilterChain
    * to override functionality mentioned in above method we've to define our own custom
    * bean of type SecurityFilterChain, so that the default won't be applicable*/
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        /*below authenticates all incoming calls*/
//        http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
        /*below permits all incoming calls without authentication, no use of adding spring
        * security dependency*/
//        http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact").permitAll());
        /*below enables default login page provided by spring security if api invoked via browser*/
        http.formLogin(Customizer.withDefaults());
        /*use below if you're developing an app, whose apis will only invoked via other services
        * so that you don't need browser based login page*/
//        http.formLogin(flc -> flc.disable())
        /*use below if working with basic style of auth, -> base64*/
        http.httpBasic(Customizer.withDefaults());
        /*disable http basic style of auth, if needed like below*/
//        http.httpBasic(hbc -> hbc.disable());
        return http.build();
    }
}
