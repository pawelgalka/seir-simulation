package pl.agh.kis.seirsimulation.model.configuration;

import lombok.Data;
import pl.agh.kis.seirsimulation.model.strategy.StrategyEnum;

@Data
public class Configuration {
    //Model Config
    public static StrategyEnum DISEASE_STRATEGY = StrategyEnum.STANDARD_SEIR;
    public static double MOVING_PPL_PERC=0.15;
    public static double MOVING_PPL_SICK=0.000001;
    //Virus Config
    public static double VIRUS_MORTABILITY=0.037;
    public static double EXPOSED_TIME = 6;
    public static double INFECTED_DAYS = 12;
    public static double REPRODUCTION_NUMBER=4;

    //Poland Statistics 2019
    public static double BIRTH_RATE=10/365;
    public static double DEATH_RATE=10/365;

    public static double LATENCY = 2 + EXPOSED_TIME + INFECTED_DAYS;
    public static int MIN_RANDOM_CELL = 30;

    public static double CONTACT_RATE=REPRODUCTION_NUMBER*((DEATH_RATE+(1/EXPOSED_TIME))/(1/EXPOSED_TIME))*(DEATH_RATE+(1/INFECTED_DAYS));

    public static double floorOrCeil(double arg){
        if(arg>0){
            return Math.ceil(arg);
        }else if(arg==0){
            return 0;
        } else return Math.floor(arg);
    }

}
