package pl.agh.kis.seirsimulation.model.strategy;

import pl.agh.kis.seirsimulation.model.configuration.Configuration;

public class StrategyFactory {
    private StrategyFactory() {
    }
    public static DiseaseStrategy getStrategy(){
        switch(Configuration.DISEASE_STRATEGY){
            case SARS_COV_2:
                return new SarsCov2Strategy();
            default:
                return new SeirStrategy();
        }
    }
}
