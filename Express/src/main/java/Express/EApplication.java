package Express;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"Express", "Core"})
public class EApplication {
    public static void main(String[] args) {
        SpringApplication.run(EApplication.class);
    }
}
