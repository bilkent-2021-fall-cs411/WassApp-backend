package tr.com.bilkent.wassapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import static javax.servlet.http.HttpServletResponse.*;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/api/**").authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler((req, resp, ex) -> resp.setStatus(SC_FORBIDDEN))
                .authenticationEntryPoint((req, resp, ex) -> resp.setStatus(SC_UNAUTHORIZED)).and()
                .formLogin()
                .loginProcessingUrl("/login")
                .successHandler((req, resp, auth) -> resp.setStatus(SC_OK))
                .failureHandler((req, resp, ex) -> resp.setStatus(SC_FORBIDDEN)).and()
                .sessionManagement()
                .invalidSessionStrategy((req, resp) -> resp.setStatus(SC_UNAUTHORIZED))
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

}
