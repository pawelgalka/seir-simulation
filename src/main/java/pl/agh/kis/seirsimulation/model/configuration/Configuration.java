package pl.agh.kis.seirsimulation.model.configuration;

import lombok.Data;
import pl.agh.kis.seirsimulation.model.configuration.disease.DiseaseConfig;
import pl.agh.kis.seirsimulation.model.strategy.StrategyEnum;

@Data
@org.springframework.context.annotation.Configuration
public class Configuration {

    //Model Config
    public static final StrategyEnum DISEASE_STRATEGY = StrategyEnum.NO_VITAL_SEIR;
    public static final double MOVING_PPL_PERC = 0.25;
    public static final double MOVING_PPL_SICK = 0.1;
    //Poland Statistics 2019
    public static final double BIRTH_RATE = 10. / 365;
    public static final double DEATH_RATE = 10. / 365;
    public static final int MIN_RANDOM_CELL = 100;
    //Virus Config
    public static double BASE_MORTALITY;
    public static DiseaseConfig DISEASE_CONFIG;
    public static int MAX_HOSPITAL;

    public static double CONTACT_RATE() {
        return DISEASE_CONFIG.getReproduction() * ((DEATH_RATE + (1. / DISEASE_CONFIG.getIncubation())) / (1.
                / DISEASE_CONFIG.getIncubation())) * (DEATH_RATE + (1. / DISEASE_CONFIG
                .getInfection()));
    }

    public static double CONTACT_RATE_NO_VITAL() {
        return DISEASE_CONFIG.getReproduction() * (1. / DISEASE_CONFIG.getInfection());
    }

    public static void setDiseaseConfig(DiseaseConfig diseaseConfig) {
        DISEASE_CONFIG = diseaseConfig;
    }

    public static void setMaxHospital(String country) {
        switch (country) {
            case "poland":
                MAX_HOSPITAL = 10000;
            default:
                MAX_HOSPITAL = 10000;
        }
    }

    public static double floorOrCeil(double arg) {
        if (arg > 0) {
            return Math.ceil(arg);
        } else if (arg == 0) {
            return 0;
        } else
            return Math.floor(arg);
    }

    public static double ceilOrFloor(double arg) {
        if (arg > 0) {
            return Math.floor(arg);
        } else if (arg == 0) {
            return 0;
        } else
            return Math.ceil(arg);
    }
}
