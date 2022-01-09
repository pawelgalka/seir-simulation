package pl.agh.kis.seirsimulation.model.configuration.disease;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.agh.kis.seirsimulation.controller.DistancingLevel;
import pl.agh.kis.seirsimulation.controller.GuiContext;

@Slf4j
@Getter
public enum DiseaseConfig {
    FLU(2., 6., 365., 0.001, 1.3, 0.625, 0.3125, 0.00069),
    AH1N1(2., 6., 365., 0.0002, 1.5, 0.75, 0.375, 0.00075),
    SARS(5., 14., 365., 0.095, 3., 1.5, 0.75, 0.12),
    COVID19(6., 16., 180., 0.03, 2.5, 1.25, 0.625, 0.15);

    public static int iterator = 0;
    static GuiContext guiContext;
    private static final double reproductionStep = 0.2;
    private final double incubation;
    private final double infection;
    private final double immunity;
    @Getter(AccessLevel.NONE)
    private final double reproduction;
    private final double reproductionDistancing1;
    private final double reproductionDistancing2;
    private final double hospitalizationPerc;
    @Setter private double mortality;
    DiseaseConfig(double incubation, double infection, double immunity, double mortality, double reproduction,
                  double reproductionDistancing1, double reproductionDistancing2, double hospitalizationPerc) {
        this.incubation = incubation;
        this.infection = infection;
        this.immunity = immunity;
        this.mortality = mortality;
        this.reproduction = reproduction;
        this.reproductionDistancing1 = reproductionDistancing1;
        this.reproductionDistancing2 = reproductionDistancing2;
        this.hospitalizationPerc = hospitalizationPerc;
    }

    public static double retrieveReproductionBasedOnDistancingLevel(
            DiseaseConfig diseaseConfig,
            DistancingLevel distancingLevel) {
        if (distancingLevel == DistancingLevel.LEVEL1) {
            return diseaseConfig.reproductionDistancing1;
        } else if (distancingLevel == DistancingLevel.LEVEL2) {
            return diseaseConfig.reproductionDistancing2;
        } else
            return diseaseConfig.reproduction;
    }

    public double getReproduction() {
        if (guiContext.getDistancingLevelChange() != null) {
            return calculateReproduction(guiContext.getDistancingLevel(), guiContext.getDistancingLevelOld());
        }
        return retrieveReproductionBasedOnDistancingLevel(this, guiContext.getDistancingLevel());
    }

    private double calculateReproduction(DistancingLevel distancingLevel, DistancingLevel distancingLevelOld) {
        double larger = Math.max(retrieveReproductionBasedOnDistancingLevel(this, distancingLevel),
                retrieveReproductionBasedOnDistancingLevel(this, distancingLevelOld));
        double smaller = Math.min(retrieveReproductionBasedOnDistancingLevel(this, distancingLevel),
                retrieveReproductionBasedOnDistancingLevel(this, distancingLevelOld));
        return retrieveReproductionBasedOnDistancingLevel(this, distancingLevel)
                > retrieveReproductionBasedOnDistancingLevel(this, distancingLevelOld) ? Math.min(
                smaller + iterator * reproductionStep, larger) : Math.max(
                larger - iterator * reproductionStep, smaller);

    }

    @Component
    public static class GuiContextInjector {
        @Autowired
        private GuiContext guiContext;

        @PostConstruct
        public void postConstruct() {
            DiseaseConfig.guiContext = guiContext;
        }
    }
}
