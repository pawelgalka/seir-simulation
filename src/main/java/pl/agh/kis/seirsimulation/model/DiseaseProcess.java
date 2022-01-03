package pl.agh.kis.seirsimulation.model;

import static pl.agh.kis.seirsimulation.model.configuration.Configuration.ceilOrFloor;
import static pl.agh.kis.seirsimulation.model.data.DataValidator.validateAppliedChanges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pl.agh.kis.seirsimulation.controller.GuiContext;
import pl.agh.kis.seirsimulation.model.data.DataValidator;
import pl.agh.kis.seirsimulation.model.strategy.DiseaseStrategy;

@Slf4j
@Component
public class DiseaseProcess {

    @Autowired
    GuiContext context;

    @Autowired
    DiseaseStrategy diseaseStrategy;

    public void simulateDayAtSingleCell(Cell cell) {
        int[] changes = diseaseStrategy
                .getDailyChanges(cell);
        context.setNotChanging(context.isNotChanging() && Arrays.stream(changes).allMatch(change -> change == 0));
        Map<Pair<Integer, Integer>, List<Integer>> immigrants = cell.getImmigrants();
        List<Integer> immigrantChangesSum = new ArrayList<>(Collections.nCopies(5, 0));
        int cellPeopleSum = cell.getStateCountMap().stream().mapToInt(Integer::intValue).sum();
        for (Pair<Integer, Integer> key : immigrants.keySet()) {
            List<Integer> immigrantsFrom = new ArrayList<>(immigrants.get(key));
            int immigrantsSum = immigrantsFrom.stream().mapToInt(Integer::intValue).sum();
            int[] immigrantChanges = { 0, 0, 0, 0, 0 };
            for (int j = 0; j < immigrantChanges.length - 1; j++) {
                immigrantChanges[j] = (int) ceilOrFloor(
                        (double) (immigrantsFrom.get(j) * changes[j]) / (cellPeopleSum + immigrantsSum));
            }
            immigrantChanges = DataValidator.randomlyValidateDailyChanges(immigrantChanges);
            for (int k = 0; k < immigrantChanges.length - 1; k++) {
                immigrantsFrom.set(k, immigrantsFrom.get(k) + immigrantChanges[k]);
                immigrantChangesSum.set(k, immigrantChangesSum.get(k) + immigrantChanges[k]);
            }

            immigrants.put(key, immigrantsFrom);
        }
        cell.setImmigrants(immigrants);
        cell.setStateCountMap(validateAppliedChanges(changes, cell.getStateCountMap(), immigrantChangesSum));
    }
}
