package org.dp.demowsreactivesvr.sec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange((authorize) -> authorize
                        .pathMatchers("/**").permitAll() //TODO клиент не коннектится
//                        .pathMatchers("/resources/**", "/signup", "/about").permitAll()
//                        .pathMatchers("/admin/**").hasRole("ADMIN")
//                        .pathMatchers("/db/**").access((authentication, context) ->
//                                hasRole("ADMIN").check(authentication, context)
//                                        .filter(decision -> !decision.isGranted())
//                                        .switchIfEmpty(hasRole("DBA").check(authentication, context))
//                        )
//                        .anyExchange().denyAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService(PasswordEncoder passwordEncoder, InMemoryUserDetailsManager manager) {
        return new ReactiveUserDetailsService() {
            @Override
            public Mono<UserDetails> findByUsername(String username) {
                return Mono.just(manager.loadUserByUsername(username));
            }
        };
    }

    @Bean
    public InMemoryUserDetailsManager manager(PasswordEncoder passwordEncoder){
        InMemoryUserDetailsManager manager  = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user1").password(passwordEncoder.encode("password")).roles("USER").build());
        manager.createUser(User.withUsername("user2").password(passwordEncoder.encode("password")).roles("USER").build());
        manager.createUser(User.withUsername("admin").password(passwordEncoder.encode("password")).roles("USER", "ADMIN").build());
        return manager;
    }
}