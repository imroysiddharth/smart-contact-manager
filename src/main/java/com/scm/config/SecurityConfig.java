package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.scm.services.impl.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {
// Setting password by java using credential in memory
    // @Bean
    // public UserDetailsService userDetailsService() {

    // UserDetails user1 = User
    // .withDefaultPasswordEncoder()
    // .username("admin123")
    // .password("admin123")
    // .roles("ADMIN", "USER")
    // .build();

    // UserDetails user2 = User
    // .withDefaultPasswordEncoder()
    // .username("user123")
    // .password("password")
    // // .roles(null)
    // .build();

    // var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(user1,
    // user2);
    // return inMemoryUserDetailsManager;

    // }
    @Autowired
    SecurityCustomUserDetailService userDetailService ;
    @Autowired
    private OAuthAuthenicationSuccessHandler handler;
    // Config of AUTHENTICATION provider    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // user detail service ka object:
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        // password encoder ka object
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        //Configs
        //Url Configs 
        httpSecurity.authorizeHttpRequests(authorize->{
            // authorize.requestMatchers("/home").permitAll();
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        //Form Default login 
        //Agr hame kuch change krna hua to yaha krnge : Form login se related 
       httpSecurity.formLogin(  formLogin -> {
            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/authenticate");
            // formLogin.successForwardUrl("/user/dashboard");
            formLogin.defaultSuccessUrl("/user/profile", true); // <- changed line
            // formLogin.failureForwardUrl("/login?error=true");
            // formLogin.defaultSuccessUrl("/home");
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");

       });

       httpSecurity.csrf(AbstractHttpConfigurer::disable);

       httpSecurity.logout(logoutForm->{
        logoutForm.logoutUrl("/do-logout");
        logoutForm.logoutSuccessUrl("/login?logout=true");
       });
        
       
        httpSecurity.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(handler);
        });

       return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
