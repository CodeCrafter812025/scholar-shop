package net.holosen.app;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan(basePackages = {"net.holosen.*"})
@EntityScan(basePackages = {"net.holosen.dataaccess.entity"})
@EnableJpaRepositories(basePackages = {"net.holosen.dataaccess.repository"})
@OpenAPIDefinition(info = @Info(title = "OnlineShop API", version = "1.0" , description = "Java Spring Boot Online Shop API"))
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}
