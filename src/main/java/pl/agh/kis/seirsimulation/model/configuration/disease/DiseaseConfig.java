package pl.agh.kis.seirsimulation.model.configuration.disease;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.controller.DistancingLevel;
import pl.agh.kis.seirsimulation.controller.GuiContext;

import javax.annotation.PostConstruct;

@Getter
public enum DiseaseConfig {
    FLU(2.,6.,0.001,1.3,0.625,0.3125),
    AH1N1(2.,6.,0.0002,1.5,0.75,0.375 ),
    SARS(5.,14.,0.095,3.,1.5,0.75),
    COVID19(6.,16.,0.034,2.5,1.25,0.625);

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
    private final double reproductionDistancing1;
    private final double reproductionDistancing2;

    public double getReproduction() {
        if(guiContext.getDistancingLevel()== DistancingLevel.LEVEL1){
            return reproductionDistancing1;
        }else if (guiContext.getDistancingLevel()== DistancingLevel.LEVEL2){
            return reproductionDistancing2;
        }
        else return reproduction;
    }

    DiseaseConfig(double incubation, double infection, double mortality, double reproduction, double reproductionDistancing1, double reproductionDistancing2) {
        this.incubation = incubation;
        this.infection = infection;
        this.mortality = mortality;
        this.reproduction = reproduction;
        this.reproductionDistancing1 = reproductionDistancing1;
        this.reproductionDistancing2 = reproductionDistancing2;
    }
}
