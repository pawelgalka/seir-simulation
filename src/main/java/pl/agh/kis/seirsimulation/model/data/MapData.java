package pl.agh.kis.seirsimulation.model.data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import pl.agh.kis.seirsimulation.model.Cell;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Getter
@Setter
public class MapData {
    static List<List<Cell>> gridMap;
    static List<Pair<Integer, Integer>> cellsForRandomIllness = new ArrayList<>();

    public static Integer getMaxValue() {
        return maxValue;
    }

    public static void setMaxValue(Integer maxValue) {
        MapData.maxValue = maxValue;
    }

    static Integer maxValue;

    public static List<List<Cell>> getGridMap() {
        return gridMap;
    }

    public static void setGridMap(List<List<Cell>> collect) {
        gridMap = collect;
    }

    public static void addRandomIndex(Pair<Integer, Integer> coordinates) {
        cellsForRandomIllness.add(coordinates);
    }

    public static Pair<Integer, Integer> getRandomIndex(int index) {
        return cellsForRandomIllness.get(index);
    }

    public static Cell getCellAtIndex(Pair<Integer, Integer> index) {
        return gridMap.get(index.getValue1()).get(index.getValue0());
    }

    public static int getRandomSize() {
        return cellsForRandomIllness.size();
    }

    public static List<Pair<Integer,Integer>> getNeighboursOfCell(Pair<Integer, Integer> indexOfCell /*x,y*/) {
        boolean possibleUp = false, possibleDown = false, possibleLeft = false, possibleRight = false;
        List<Pair<Integer,Integer>> neighbours = new ArrayList<>();
        if (indexOfCell.getValue1() /*y*/ >= 1) {
            possibleUp = true;
            neighbours.add(new Pair<>(indexOfCell.getValue0(), indexOfCell.getValue1() - 1));
        }
        if (indexOfCell.getValue1() < gridMap.size() - 1) {
            possibleDown = true;
            neighbours.add(new Pair<>(indexOfCell.getValue0(), indexOfCell.getValue1() + 1));
        }
        if (indexOfCell.getValue0() /*x*/ >= 1) {
            possibleLeft = true;
            neighbours.add(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1()));
        }
        if (indexOfCell.getValue0() < gridMap.get(0).size() - 1) {
            possibleRight = true;
            neighbours.add(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1()));
        }
        if (possibleUp && possibleLeft) {
            neighbours.add(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1() - 1));
        }
        if (possibleUp && possibleRight) {
            neighbours.add(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1() - 1));
        }
        if (possibleDown && possibleLeft) {
            neighbours.add(new Pair<>(indexOfCell.getValue0() - 1, indexOfCell.getValue1() + 1));
        }
        if (possibleDown && possibleRight) {
            neighbours.add(new Pair<>(indexOfCell.getValue0() + 1, indexOfCell.getValue1() + 1));
        }
        return neighbours;
    }
}
