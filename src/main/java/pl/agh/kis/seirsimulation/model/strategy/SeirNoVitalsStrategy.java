package pl.agh.kis.seirsimulation.model.strategy;

import pl.agh.kis.seirsimulation.model.Cell;

import java.util.List;

import static pl.agh.kis.seirsimulation.model.configuration.Configuration.*;
import static pl.agh.kis.seirsimulation.model.data.ChangesValidator.randomlyValidateDailyChanges;

public class SeirNoVitalsStrategy implements DiseaseStrategy {

    @Override
    public int calculateSusceptibleChange(List<Integer> stateCount, double stateCountSum) {
        return (int) floorOrCeil(
                ((-CONTACT_RATE_NO_VITAL() * (double) stateCount.get(2) * (double) stateCount.get(0)) / stateCountSum));
    }

    @Override
    public int calculateExposedChange(List<Integer> stateCount, double stateCountSum) {
        return (int) floorOrCeil(
                (CONTACT_RATE_NO_VITAL() * stateCount.get(2) * stateCount.get(0)) / stateCountSum - ((1 / DISEASE_CONFIG
                        .getIncubation()) * stateCount.get(1)));
    }

    @Override
    public int calculateInfectedChange(List<Integer> stateCount) {
        return (int) floorOrCeil(((1. / DISEASE_CONFIG.getIncubation()) * stateCount.get(1)) - (((1. / DISEASE_CONFIG
                .getInfection())) * stateCount.get(2)));
    }

    @Override
    public int calculateRecoveredChange(List<Integer> stateCount) {
        return (int) floorOrCeil((1. / DISEASE_CONFIG.getInfection()) * stateCount.get(2));
    }

    @Override
    public int calculateDiseaseDeaths(List<Integer> stateCount) {
        return (int) Math.round(calculateRecoveredChange(stateCount) * DISEASE_CONFIG.getMortality());
    }

    @Override
    public int[] getDailyChanges(Cell cell) {
        var stateCount = cell.getStateCountMap();
        var stateCountSum = (double) cell.getStateCountMap().stream().mapToInt(Integer::intValue).sum();
        return randomlyValidateDailyChanges(new int[] { calculateSusceptibleChange(stateCount, stateCountSum),
                calculateExposedChange(stateCount, stateCountSum), calculateInfectedChange(stateCount),
                calculateRecoveredChange(stateCount) - calculateDiseaseDeaths(stateCount),
                calculateDiseaseDeaths(stateCount) });
    }
}
