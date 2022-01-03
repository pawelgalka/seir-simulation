package pl.agh.kis.seirsimulation.model.strategy;

import java.util.List;

import pl.agh.kis.seirsimulation.model.Cell;

public class SarsCov2Strategy implements DiseaseStrategy {
    @Override
    public int calculateSusceptibleChange(List<Integer> stateCount, double stateCountSum,
            boolean lessThanHundredInfected) {
        return 0;
    }

    @Override
    public int calculateExposedChange(List<Integer> stateCount, double stateCountSum, boolean lessThanHundredInfected) {
        return 0;
    }

    @Override
    public int calculateInfectedChange(List<Integer> stateCount, boolean lessThanHundredInfected) {
        return 0;
    }

    @Override
    public int calculateRecoveredChange(List<Integer> stateCount, boolean lessThanHundredInfected) {
        return 0;
    }

    @Override
    public int calculateDiseaseDeaths(List<Integer> stateCount, boolean lessThanHundredInfected) {
        return 0;
    }

    @Override
    public int[] getDailyChanges(Cell cell) {
        return new int[0];
    }
}
