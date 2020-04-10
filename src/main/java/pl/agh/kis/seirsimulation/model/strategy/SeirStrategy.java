package pl.agh.kis.seirsimulation.model.strategy;

import java.util.List;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.*;

public class SeirStrategy implements DiseaseStrategy {

    @Override
    public int calculateSusceptibleChange(List<Integer> stateCount, int stateCountSum) {
        return (int) Math.ceil((BIRTH_RATE*stateCountSum)-(DEATH_RATE*stateCount.get(0))-((CONTACT_RATE*stateCount.get(2)*stateCount.get(0))/stateCountSum));
    }

    @Override
    public int calculateExposedChange(List<Integer> stateCount, int stateCountSum) {
        return (int)Math.ceil((CONTACT_RATE*stateCount.get(2)*stateCount.get(0))/stateCountSum - (DEATH_RATE+1/EXPOSED_TIME)*stateCount.get(1));
    }

    @Override
    public int calculateInfectedChange(List<Integer> stateCount) {
        return (int)Math.ceil(((1/EXPOSED_TIME)*stateCount.get(1))-(((1/INFECTED_DAYS)+DEATH_RATE)*stateCount.get(2)));
    }

    @Override
    public int calculateRecoveredChange(List<Integer> stateCount) {
        return (int)Math.ceil((1/INFECTED_DAYS)*stateCount.get(2)-(DEATH_RATE*stateCount.get(3)));
    }

    @Override
    public int calculateDiseaseDeaths(List<Integer> stateCount) {
        return (int)Math.round(calculateRecoveredChange(stateCount)*VIRUS_MORTABILITY);
    }

    @Override
    public int[] getDailyChanges(List<Integer> stateCount, int stateCountSum) {
        return new int[]{calculateSusceptibleChange(stateCount,stateCountSum),calculateExposedChange(stateCount,stateCountSum),calculateInfectedChange(stateCount),calculateRecoveredChange(stateCount)-calculateDiseaseDeaths(stateCount)};
    }
}

