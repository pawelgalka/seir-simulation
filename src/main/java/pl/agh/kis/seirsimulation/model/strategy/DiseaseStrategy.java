package pl.agh.kis.seirsimulation.model.strategy;

import pl.agh.kis.seirsimulation.model.Cell;

import java.util.List;

public interface DiseaseStrategy {
    int calculateSusceptibleChange(List<Integer> stateCount,int stateCountSum);
    int calculateExposedChange(List<Integer> stateCount,int stateCountSum);
    int calculateInfectedChange(List<Integer> stateCount);
    int calculateRecoveredChange(List<Integer> stateCount);
    int calculateDiseaseDeaths(List<Integer> stateCount);
    int[] getDailyChanges(Cell cell);
}
