package pl.agh.kis.seirsimulation.model.configuration.disease;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.controller.GuiContext;

import javax.annotation.PostConstruct;

@Getter
public enum DiseaseConfig {
    FLU(2.,6.,0.001,1.3),
    AH1N1(2.,6.,0.0002,1.5),
    SARS(5.,14.,0.095,3.),
    COVID19(6.,16.,0.034,3.9);

    @Component
    public static class GuiContextInjector {
        @Autowired
        private GuiContext guiContext;

        @PostConstruct
        public void postConstruct() {
            DiseaseConfig.guiContext = guiContext;
        }
    }

    static GuiContext guiContext;

    private final double incubation;
    private final double infection;
    @Setter private double mortality;
    @Getter(AccessLevel.NONE)
    private final double reproduction;

    public double getReproduction() {
        return guiContext.isDistancing() ?
                0.8 :
                reproduction;
    }

    DiseaseConfig(double incubation, double infection, double mortality, double reproduction) {
        this.incubation = incubation;
        this.infection = infection;
        this.mortality = mortality;
        this.reproduction = reproduction;
    }
}
