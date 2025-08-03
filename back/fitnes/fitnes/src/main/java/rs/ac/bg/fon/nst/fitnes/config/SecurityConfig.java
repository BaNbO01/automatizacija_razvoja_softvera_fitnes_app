package rs.ac.bg.fon.nst.fitnes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import rs.ac.bg.fon.nst.fitnes.security.JwtAuthEntryPoint;
import rs.ac.bg.fon.nst.fitnes.security.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity 
public class SecurityConfig {

    private final JwtAuthEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter authenticationFilter;

    public SecurityConfig(JwtAuthEntryPoint authenticationEntryPoint,
                          JwtAuthenticationFilter authenticationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }

  
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

   
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); 
        configuration.setMaxAge(3600L); 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) 
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
                .authorizeHttpRequests(authorize -> {
                   
                    authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                  
                    authorize.requestMatchers("/api/auth/**").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/api/vezbe/{id}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/api/vezbe/video/{id}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/api/grupe-misica").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/api/grupe-misica/{id}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/api/kategorije-vezbe").permitAll();
                    authorize.requestMatchers("/api/uploads/**").permitAll();


              
                    authorize.requestMatchers("/api/users/treneri/**").hasRole("ADMIN");
                    authorize.requestMatchers("/api/users/vezbaci/**").hasRole("ADMIN");
                    authorize.requestMatchers(HttpMethod.POST, "/api/grupe-misica").hasRole("ADMIN");

                 
                    authorize.requestMatchers("/api/vezbe").hasAnyRole("TRENER", "VEZBAC"); 
                    authorize.requestMatchers(HttpMethod.POST, "/api/vezbe").hasRole("TRENER");
                    authorize.requestMatchers(HttpMethod.PUT, "/api/vezbe/{id}").hasRole("TRENER");


                   
                    authorize.requestMatchers("/api/users/dnevnici").hasRole("VEZBAC");
                    authorize.requestMatchers("/api/dnevnici/**").hasRole("VEZBAC");
                    authorize.requestMatchers("/api/plan-treninga/**").hasRole("VEZBAC");


                  
                    authorize.anyRequest().authenticated();
                });


        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
