package com.stkbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

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

    @Bean
    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withUsername("user").password("12345").roles("user").build();
//        UserDetails admin = User.withUsername("admin").password("54321").roles("admin").build();
        /*If we directly use like above, setting password 12345, will cause exception
        * spring security wants us to explicitly define which kind of password encoding we're trying to use
        * so if we're working with NoOpPasswordEncoder, which allows plaintexts, we should set password like
        * {noop}12345, and also if we don't mention any kind of encoder prefix to a password, then the default
        * password encoder will be assumed, which is BCryptPasswordEncoder for now.*/
//        UserDetails user = User.withUsername("user").password("{noop}12345").roles("user").build();
        /*below is the bcrypt hash representing 54321*/
//        UserDetails admin = User.withUsername("admin").password("$2a$12$ZSQo.WCTo9rVON3d5uVYV.ofrrczNklfBHyUG/d0scfaqJTlQk62a").roles("admin").build();
        /*Using above caused compromised password issue*/
        UserDetails user = User.withUsername("user").password("{noop}stk@12345").roles("user").build();
        UserDetails admin = User.withUsername("admin")
                .password("{bcrypt}$2a$12$ZsI0Yhl2o3PcLoYDvAHoF.hNpvCQtTs.ljsbkdyTmZE8LrKsEjm0m")
                .roles("admin").build();
        /*InMemoryUserDetailsManager uses map data structure to store use details*/
        return new InMemoryUserDetailsManager(user, admin);
    }

    /*Below must be used to store passwords in encrypted passwords, else
    * we might need to store passwords in plaintext manner. If we don't use
    * below, by default spring security will throw exception, it expects this should be
    * there.*/
    @Bean
    public PasswordEncoder passwordEncoder(){
        /*Instead of hardcoding like new BCryptPasswordEncoder()
        * we should use below, because its a factory method provided by
        * spring sec team, which will use a passwordencoder which meets latest
        * security requirements, for now it is BCryptPasswordEncoder, which
        * can change with changing security requirements*/
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /*Below is available from spring 6.3 release, which helps us identify weak passwords,
    * if any password user enters has been already identified in a data breach, this bean will throw
    * an exception saying password has been compromised, HaveIBeenPwnedRestApiPasswordChecker is the default
    * implementation provided by spring sec for CompromisedPasswordChecker, which internally calls an api
    * to check if provided password is weak or not.*/
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker(){
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
