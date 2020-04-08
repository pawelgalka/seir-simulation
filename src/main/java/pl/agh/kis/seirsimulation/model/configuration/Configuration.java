package pl.agh.kis.seirsimulation.model.configuration;

import lombok.Data;

@Data
public class Configuration {
    //Virus Config
    public static double VIRUS_MORTABILITY=0.037;
    public static boolean DEADLY_WITHOUT_SYMPTOMS;
    public static double EXPOSED_TIME = 2;
    public static double INFECTED_DAYS = 4;
    public static double REPRODUCTION_NUMBER=4;

    //Poland Statistics 2019
    public static double BIRTH_RATE=10.46/365;
    public static double DEATH_RATE=10.48/365;

    public static double LATENCY = 2 + EXPOSED_TIME + INFECTED_DAYS;
    public static int MIN_RANDOM_CELL = 30;

    public static double CONTACT_RATE=REPRODUCTION_NUMBER*((DEATH_RATE+(1/EXPOSED_TIME))/(1/EXPOSED_TIME))*(DEATH_RATE+(1/INFECTED_DAYS));
}
