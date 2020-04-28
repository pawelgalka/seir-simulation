package pl.agh.kis.seirsimulation.model.configuration.disease;

import lombok.Getter;

@Getter
public enum DiseaseConfig {
    FLU(2,6,0.1,1.3),
    AH1N1(2,6,0.02,1.5),
    SARS(5,14,9.5,3),
    COVID19(6,16,3.4,3.9);

    private final int incubation;
    private final int infection;
    private final double mortality;
    private final double reproduction;

    DiseaseConfig(int incubation, int infection, double mortality, double reproduction) {
        this.incubation = incubation;
        this.infection = infection;
        this.mortality = mortality;
        this.reproduction = reproduction;
    }
}
