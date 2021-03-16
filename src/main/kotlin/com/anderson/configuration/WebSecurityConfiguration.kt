package com.anderson.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration : WebSecurityConfigurerAdapter(){

    override fun configure(http : HttpSecurity){
        http.httpBasic()
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {
        val anderson = User.withDefaultPasswordEncoder()
            .username("Anderson")
            .password("anderson")
            .roles("USER")
            .build()

        val manager = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("administrator")
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(anderson, manager)
    }
}