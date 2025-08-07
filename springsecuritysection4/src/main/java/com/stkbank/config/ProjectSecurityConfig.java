package com.stkbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import javax.sql.DataSource;

@Configuration
public class ProjectSecurityConfig {
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests(requests -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact", "/register").permitAll());
        http.formLogin(Customizer.withDefaults());
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /*Below bean is used when working with JDBCUserDetailsManager, this time the user details will
     * be stored in db, instead of using in memory, JDBC needs information regarding which database url, username etc., it should connect
     * to, that's why we give datasource object to it while creating a bean, datasource object will be auto created
     * based on the props given in properties file, like spring.datasource.url etc.,
     * One major drawback is that, we're forced to use similar table structures provided by jdbc,
     * which doesn't allow flexibility, the same scripts are located under directory
     * org/springframework/security/core/userdetails/jdbc/users.ddl
     * Execute the scritps and you're ready to go.*/
//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }
    /*commented above because, now we're using customUserDetailsManager by implementing UserDetailsService
    * If not commented, spring will throw ambiguity exception, stating two implementations for
    * UserDetailsService found, which one should I inject.*/

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
