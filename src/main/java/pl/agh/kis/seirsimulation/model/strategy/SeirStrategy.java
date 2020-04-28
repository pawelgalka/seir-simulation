package pl.agh.kis.seirsimulation.model.strategy;

import pl.agh.kis.seirsimulation.model.Cell;
import java.util.List;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.*;
import static pl.agh.kis.seirsimulation.model.data.ChangesValidator.randomlyValidateDailyChanges;

public class SeirStrategy implements DiseaseStrategy {

    @Override
    public int calculateSusceptibleChange(List<Integer> stateCount, double stateCountSum) {
        System.out.println((BIRTH_RATE*stateCountSum)-(DEATH_RATE*stateCount.get(0))-((CONTACT_RATE*stateCount.get(2)*stateCount.get(0))/stateCountSum));
        return (int) floorOrCeil((BIRTH_RATE*stateCountSum)-(DEATH_RATE*stateCount.get(0))-((CONTACT_RATE*stateCount.get(2)*stateCount.get(0))/stateCountSum));
    }

    @Override
    public int calculateExposedChange(List<Integer> stateCount, double stateCountSum) {
        return (int) floorOrCeil((CONTACT_RATE*stateCount.get(2)*stateCount.get(0))/stateCountSum - (DEATH_RATE+1/EXPOSED_TIME)*stateCount.get(1));
    }

    @Override
    public int calculateInfectedChange(List<Integer> stateCount) {
        return (int) floorOrCeil(((1/EXPOSED_TIME)*stateCount.get(1))-(((1/INFECTED_DAYS)+DEATH_RATE)*stateCount.get(2)));
    }

    @Override
    public int calculateRecoveredChange(List<Integer> stateCount) {
        return (int) floorOrCeil((1/INFECTED_DAYS)*stateCount.get(2)-(DEATH_RATE*stateCount.get(3)));
    }

    @Override
    public int calculateDiseaseDeaths(List<Integer> stateCount) {
        return (int) floorOrCeil(calculateRecoveredChange(stateCount)*VIRUS_MORTABILITY);
    }

    @Override
    public int[] getDailyChanges(Cell cell) {
        var stateCount = cell.getStateCountMap();
        var stateCountSum = cell.getStateCountMap().stream().mapToInt(Integer::intValue).sum();
        return randomlyValidateDailyChanges(new int[]{calculateSusceptibleChange(stateCount,stateCountSum),calculateExposedChange(stateCount,stateCountSum),calculateInfectedChange(stateCount),calculateRecoveredChange(stateCount),calculateDiseaseDeaths(stateCount)});
    }
}

