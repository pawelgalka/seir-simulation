package pl.agh.kis.seirsimulation.model.configuration;

import static pl.agh.kis.seirsimulation.model.strategy.StrategyFactory.getStrategy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pl.agh.kis.seirsimulation.controller.GuiContext;
import pl.agh.kis.seirsimulation.model.Simulation;
import pl.agh.kis.seirsimulation.model.data.DataLoader;
import pl.agh.kis.seirsimulation.model.strategy.DiseaseStrategy;

@Configuration
public class BeanFactory {

    @Bean DataLoader dataLoader() {
        return new DataLoader();
    }

    @Bean Simulation simulation() {
        return new Simulation();
    }

    @Bean GuiContext guiContext() {
        return new GuiContext();
    }

    @Bean DiseaseStrategy diseaseStrategy() {
        return getStrategy();
    }
}
