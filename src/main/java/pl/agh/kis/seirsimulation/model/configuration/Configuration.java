package pl.agh.kis.seirsimulation.model.configuration;

import lombok.Data;

@Data
public class Configuration {
    public static double BIRTH_RATE;
    public static double DEATH_RATE;
    public static double VIRUS_MORTABILITY;
    public static int EXPOSED_TIME = 2;
    public static int INFECTED_DAYS = 4;
    public static int LATENCY = 2 + EXPOSED_TIME + INFECTED_DAYS;
    public static int MIN_RANDOM_CELL = 30;
}
