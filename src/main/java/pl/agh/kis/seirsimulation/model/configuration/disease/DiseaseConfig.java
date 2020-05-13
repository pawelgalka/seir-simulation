package pl.agh.kis.seirsimulation.model.configuration.disease;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum DiseaseConfig {
    FLU(2.,6.,0.001,1.3),
    AH1N1(2.,6.,0.0002,1.5),
    SARS(5.,14.,0.095,3.),
    COVID19(6.,16.,0.034,3.9);

    private final double incubation;
    private final double infection;
    @Setter private double mortality;
    private final double reproduction;

    DiseaseConfig(double incubation, double infection, double mortality, double reproduction) {
        this.incubation = incubation;
        this.infection = infection;
        this.mortality = mortality;
        this.reproduction = reproduction;
    }
}
