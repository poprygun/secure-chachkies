package io.microsamples.security.securechachkies.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.StringJoiner;

@Configuration
@EnableWebSecurity
/**
 * todo figure out sessions management
 * todo will need to add profile for cloud deployment
 */
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("user1")
                .password("{noop}password")
                .authorities("chachkies.user")
                .and()

                .withUser("user2")
                .password("{noop}password")
                .authorities("chachkies.user")
                .and()


                .withUser("admin1")
                .password("{noop}password")
                .authorities("chachkies.admin")
                .and()

                .withUser("admin2")
                .password("{noop}password")
                .authorities("chachkies.admin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        restrictHttpVerbs(http);
        requiresHttps(http);
        setupBasicAuth(http);
        setupRoles(http);
        useHttpStrictTransportSecurity(http);
        configureCsrfProtection(http);
        preventCrossSiteScripting(http);
        createContentSecurityPolicyHeader(http);
        preventMultipleSessions(http);
    }

    private void preventMultipleSessions(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/logout");
    }

    private void restrictHttpVerbs(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.TRACE, "/**").denyAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").denyAll()
                .antMatchers(HttpMethod.PATCH, "/**").denyAll()
                .antMatchers(HttpMethod.HEAD, "/**").denyAll() ;
    }

    private void createContentSecurityPolicyHeader(HttpSecurity http) throws Exception {
        var cspJoiner = new StringJoiner(";");
//        cspJoiner.add("default-src 'self' " + ssoServiceUrl);
        cspJoiner.add("script-src 'self' 'unsafe-inline' 'unsafe-eval'");
        cspJoiner.add("style-src 'self' 'unsafe-inline'");
        cspJoiner.add("frame-src 'none'");
        cspJoiner.add("img-src 'self' data:");
        cspJoiner.add("font-src 'self' data:");
        cspJoiner.add("sandbox allow-forms allow-scripts allow-same-origin allow-downloads");
        var csp = cspJoiner.toString();

        http.headers().contentSecurityPolicy(csp);
    }

    private void preventCrossSiteScripting(HttpSecurity http) throws Exception {
        http
                .headers()
                .contentTypeOptions().and()
                .xssProtection()
                .xssProtectionEnabled(true)
                .block(true);
    }

    private void configureCsrfProtection(HttpSecurity http) throws Exception {
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

    private void useHttpStrictTransportSecurity(HttpSecurity http) throws Exception {
        http
                .headers()
                .httpStrictTransportSecurity()
                .includeSubDomains(true)
                .maxAgeInSeconds(1024000);
    }

    private void setupBasicAuth(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }

    private void requiresHttps(HttpSecurity http) throws Exception {
        http.requiresChannel().anyRequest().requiresSecure();
    }

    private void setupRoles(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/login", "/welcome", "/public/*", "/favicon.ico", "/static/**")
                .permitAll()

                .antMatchers("/api/chachkies/**")
                .hasAnyAuthority("chachkies.user")

                .antMatchers("/actuator/**")
                .hasAnyAuthority("chachkies.admin")

                .anyRequest()
                .hasAnyAuthority("chachkies.user", "chachkies.admin");
    }

}
