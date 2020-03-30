package pl.agh.kis.seirsimulation;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SeirSimulationApplication extends javafx.application.Application {

    @Override
    public void init() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SeirSimulationApplication.class);
        builder.run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override public void start(Stage stage) {
        StackPane root = new StackPane();
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }
}
