package pl.agh.kis.seirsimulation.model;

import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.controller.GuiContext;
import pl.agh.kis.seirsimulation.model.data.DataValidator;
import pl.agh.kis.seirsimulation.model.strategy.DiseaseStrategy;

import java.util.*;

import static java.lang.Math.round;
import static pl.agh.kis.seirsimulation.model.data.DataValidator.validateAppliedChanges;

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
        List<Integer> immigrantChangesSum = new ArrayList<>(Collections.nCopies(4, 0));
        var cellPeopleSum = cell.getStateCountMap().stream().mapToInt(Integer::intValue).sum();
        for (var key : immigrants.keySet()) {
            List<Integer> immigrantsFrom = new ArrayList<>(immigrants.get(key));
            var immigrantsSum=immigrantsFrom.stream().mapToInt(Integer::intValue).sum();
            for (int j = 0; j < immigrantsFrom.size(); j++) {
                immigrantsFrom.set(j, immigrantsFrom.get(j) + (int) round(
                        (double) (immigrantsFrom.get(j) * changes[j]) / (cellPeopleSum+immigrantsSum)));
                immigrantChangesSum.set(j, immigrantChangesSum.get(j) + (int) round(
                        (double) (immigrantsFrom.get(j) * changes[j]) / (cellPeopleSum+immigrantsSum)));
            }
            immigrants.put(key, immigrantsFrom);
        }
        cell.setImmigrants(immigrants);
        cell.setD(cell.getD() + changes[changes.length-1]);
        cell.setStateCountMap(validateAppliedChanges(changes,cell.getStateCountMap(),immigrantChangesSum));
    }

}
