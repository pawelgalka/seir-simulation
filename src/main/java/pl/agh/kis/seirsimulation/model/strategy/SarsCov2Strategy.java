package pl.agh.kis.seirsimulation.model.strategy;

import pl.agh.kis.seirsimulation.model.Cell;

import java.util.List;

public class SarsCov2Strategy implements DiseaseStrategy {
    @Override
    public int calculateSusceptibleChange(List<Integer> stateCount, int stateCountSum) {
        return 0;
    }

    @Override
    public int calculateExposedChange(List<Integer> stateCount, int stateCountSum) {
        return 0;
    }

    @Override
    public int calculateInfectedChange(List<Integer> stateCount) {
        return 0;
    }

    @Override
    public int calculateRecoveredChange(List<Integer> stateCount) {
        return 0;
    }

    @Override
    public int calculateDiseaseDeaths(List<Integer> stateCount) {
        return 0;
    }

    @Override
    public int[] getDailyChanges(Cell cell) {
        return new int[0];
    }
}
