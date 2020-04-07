package pl.agh.kis.seirsimulation.model.data;

import lombok.*;
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

    public static void addRandomIndex(Pair<Integer, Integer> coordinates){
        cellsForRandomIllness.add(coordinates);
    }

    public static Pair<Integer, Integer> getRandomIndex(int index){
        return cellsForRandomIllness.get(index);
    }

    public static Cell getCellAtIndex(Pair<Integer, Integer> index){
        return gridMap.get(index.getValue0()).get(index.getValue1());
    }

    public static int getRandomSize(){
        return cellsForRandomIllness.size();
    }
}
