package pl.agh.kis.seirsimulation.model;

import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.model.data.MapData;
import pl.agh.kis.seirsimulation.model.strategy.DiseaseStrategy;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.round;
import static pl.agh.kis.seirsimulation.model.Cell.isSick;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.*;
import static pl.agh.kis.seirsimulation.model.data.MapData.getCellAtIndex;
import static pl.agh.kis.seirsimulation.model.data.MapData.getNeighboursOfCell;

@Slf4j
@Component
public class DiseaseProcess {

    @Autowired
    DiseaseStrategy diseaseStrategy;

    public void simulateDayAtSingleCell(Cell cell) {
        int[] changes = diseaseStrategy
                .getDailyChanges(cell);
        //log.debug("changes" + Arrays.toString(changes));
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
//                System.out.println((int) round((double) (immigrantsFrom.get(j) * changes[j]) / cellPeopleSum));
            }
            immigrants.put(key, immigrantsFrom);
        }
        //log.debug("changessum  "+immigrantChangesSum);
        //log.debug("immigrants" + immigrants);
        cell.setImmigrants(immigrants);
        cell.setD(cell.getD() + changes[changes.length-1]);
        for (int j = 0; j < cell.getStateCountMap().size(); j++) {
            cell.getStateCountMap().set(j, cell.getStateCountMap().get(j) + (changes[j] -immigrantChangesSum.get(j)));
        }
    }

    // TODO: 28.04.2020 move to movementExecutor class
    public void makeMove() {
        for (int y = 0; y < MapData.getGridMap().size(); y++) {
            for (int x = 0; x < MapData.getGridMap().get(0).size(); x++) {
                var neighbours = getNeighboursOfCell(new Pair<>(x, y));
                long notEmptyNeighbours = neighbours.stream()
                        .filter(e -> getCellAtIndex(e).getStateCountMap().stream().mapToInt(Integer::intValue).sum()
                                > 0).count();
                List<Integer> moving = getMovingPeople(getCellAtIndex(new Pair<>(x, y)), ((int) notEmptyNeighbours));
                for (Pair<Integer, Integer> immigrantKey : neighbours) {
                    List<Integer> immigrants = new ArrayList<>();
                    for (int j = 0; j < getCellAtIndex(new Pair<>(x, y)).getStateCountMap().size(); j++) {
                        getCellAtIndex(new Pair<>(x, y)).getStateCountMap().set(j,
                                getCellAtIndex(new Pair<>(x, y)).getStateCountMap().get(j)
                                        - moving.get(j));
                        immigrants.add(j, moving.get(j));
                    }
                    getCellAtIndex(immigrantKey).getImmigrants()
                            .put(new Pair<>(x, y), immigrants);
                }
            }
        }
    }

    // TODO: 28.04.2020 move to movementExecutor class
    private List<Integer> getMovingPeople(Cell c, int notEmptyNeighbours) {
        return IntStream.range(0, c.getStateCountMap().size()).map(index -> (int) round(
                c.getStateCountMap().get(index) * (isSick.test(index) ? MOVING_PPL_SICK : MOVING_PPL_PERC)
                        / notEmptyNeighbours)).boxed().collect(Collectors.toList());
    }

    // TODO: 28.04.2020 move to movementExecutor class
/*    public void makeMoveBack(Pair<Integer, Integer> source) {
        Cell sourceCell = getCellAtIndex(source);
        List<Integer> immigrantsFrom;
        for (var immigrantKey : sourceCell.getImmigrants().keySet()) {
            immigrantsFrom = sourceCell.getImmigrants().getOrDefault(immigrantKey, Collections.emptyList());
            List<Integer> neighbourSCM = getCellAtIndex(immigrantKey).getStateCountMap();
            for (int j = 0; j < sourceCell.getImmigrants().get(immigrantKey).size(); j++) {
                neighbourSCM.set(j, neighbourSCM.get(j) + immigrantsFrom.get(j));
            }
        }
        sourceCell.getImmigrants().clear();

    }*/

    public void makeMoveBack(Cell sourceCell) {
        List<Integer> immigrantsFrom;
        for (var immigrantKey : sourceCell.getImmigrants().keySet()) {
            immigrantsFrom = sourceCell.getImmigrants().getOrDefault(immigrantKey, Collections.emptyList());
            List<Integer> neighbourSCM = getCellAtIndex(immigrantKey).getStateCountMap();
            for (int j = 0; j < sourceCell.getImmigrants().get(immigrantKey).size(); j++) {
                neighbourSCM.set(j, neighbourSCM.get(j) + immigrantsFrom.get(j));
            }
            getCellAtIndex(immigrantKey).setStateCountMap(neighbourSCM);
        }
        sourceCell.getImmigrants().clear();

    }
}
