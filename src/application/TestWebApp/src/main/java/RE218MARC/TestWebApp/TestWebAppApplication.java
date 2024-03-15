package RE218MARC.TestWebApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;


@SpringBootApplication
@RestController
@RequestMapping("/")
public class TestWebAppApplication {
    public static void main(String[] args) {
      SpringApplication.run(TestWebAppApplication.class, args);
    }
    @GetMapping("/home")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
      return String.format("Hello %s! This is the home page !", name);
    }

}

