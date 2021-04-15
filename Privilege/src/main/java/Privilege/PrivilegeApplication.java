package Privilege;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"Privilege", "Core"})
//@MapperScan("cn.edu.xmu.compuOrg.mapper")
public class PrivilegeApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrivilegeApplication.class);
    }
}
