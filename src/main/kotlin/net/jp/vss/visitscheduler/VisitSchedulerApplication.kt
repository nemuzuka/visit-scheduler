package net.jp.vss.visitscheduler

import net.jp.vss.visitscheduler.configurations.VssConfigurationProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Application Main.
 */
@SpringBootApplication
class VisitSchedulerApplication

/**
 * メイン処理.
 */
fun main(args: Array<String>) {

    System.getenv("PORT")?.also {
        // Heroku 対応
        // 環境変数 PORT で指定した port で bind する事
        // Dockerfile の EXPOSE は無視する
        System.getProperties()["server.port"] = it
    }

    runApplication<VisitSchedulerApplication>(*args)
}

/**
 * Security に関する 設定.
 */
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var vssConfigurationProperties: VssConfigurationProperties

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers(
                "/css/**",
                "/js/**",
                "/img/**",
                "/",
                "/index.html",
                "/favicon.ico",
                "/api/open-id-connects",
                "/api/health")
            .permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .defaultSuccessUrl("/auth/approved", true)
            .and()
            .formLogin().disable()
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .csrf().csrfTokenRepository(getCsrfTokenRepository())
            .and()
            .httpBasic().disable()
            .exceptionHandling()
            .authenticationEntryPoint(VssAuthenticationEntryPoint())
    }

    /**
     * 未認証時の処理.
     */
    class VssAuthenticationEntryPoint : AuthenticationEntryPoint {
        override fun commence(
            request: HttpServletRequest?,
            response: HttpServletResponse,
            authException: AuthenticationException?
        ) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
        }
    }

    /**
     * CORS 用の設定.
     *
     * @return 設定
     */
    private fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL)
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL)
        corsConfiguration.addAllowedOrigin(vssConfigurationProperties.redirectBaseUrl)
        corsConfiguration.allowCredentials = true

        val corsConfigurationSource = UrlBasedCorsConfigurationSource()
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration)

        return corsConfigurationSource
    }

    /**
     * CsrfTokenRepository の設定.
     *
     * js からアクセスする為、Httponly を false にします。
     * @return CsrfTokenRepository
     */
    @Bean
    fun getCsrfTokenRepository(): CsrfTokenRepository {
        val tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
        tokenRepository.cookiePath = "/"
        return tokenRepository
    }
}
