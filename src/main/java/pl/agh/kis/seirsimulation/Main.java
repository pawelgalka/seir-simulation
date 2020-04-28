package pl.agh.kis.seirsimulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pl.agh.kis.seirsimulation.model.DiseaseProcess;

@SpringBootApplication
public class Main {

    private static ConfigurableApplicationContext ctx;

    public static void main(String[] args) {
        ctx = SpringApplication.run(Main.class, args);
        ((DiseaseProcess) ctx.getBean("diseaseProcess")).test();
    }
}