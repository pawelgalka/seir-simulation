package pl.agh.kis.seirsimulation.model;

import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.model.data.MapData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.round;
import static pl.agh.kis.seirsimulation.model.Cell.isSick;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.MOVING_PPL_PERC;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.MOVING_PPL_SICK;
import static pl.agh.kis.seirsimulation.model.data.MapData.*;

@Component
@Slf4j
public class Movement {

    public void makeMove() {
        for (int y = 0; y < MapData.getGridMap().size(); y++) {
            for (int x = 0; x < MapData.getGridMap().get(0).size(); x++) {
                var neighbours = getNeighboursOfCell(new Pair<>(x, y));
                neighbours.add(getRandomCellCoords(new ArrayList<>(neighbours),new Pair<>(x,y)));
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

    private List<Integer> getMovingPeople(Cell c, int notEmptyNeighbours) {
        return IntStream.range(0, c.getStateCountMap().size()).map(index -> (int) round(
                c.getStateCountMap().get(index) * (isSick.test(index) ? MOVING_PPL_SICK : MOVING_PPL_PERC)
                        / notEmptyNeighbours)).boxed().collect(Collectors.toList());
    }
}
