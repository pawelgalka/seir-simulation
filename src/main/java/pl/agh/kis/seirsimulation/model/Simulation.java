package pl.agh.kis.seirsimulation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import pl.agh.kis.seirsimulation.controller.GuiContext;
import pl.agh.kis.seirsimulation.model.data.MapData;

import java.util.Map;

@Slf4j
@NoArgsConstructor
@Getter
public class Simulation {

    @Autowired
    GuiContext context;

    @Autowired
    DiseaseProcess diseaseProcess;

    public void step() {
        log.debug("stepping");
        context.dayStep();
/*        System.out.println("before move");
        System.out.println(MapData.getCellAtIndex(new Pair<>(10,10)).getStateCountMap());
        System.out.println(MapData.getCellAtIndex(new Pair<>(10,10)).getImmigrants());*/
        diseaseProcess.makeMove();
/*        System.out.println("moved");
        System.out.println(MapData.getCellAtIndex(new Pair<>(10,10)).getStateCountMap());
        System.out.println(MapData.getCellAtIndex(new Pair<>(10,10)).getImmigrants());*/
        for (var row : MapData.getGridMap()){
            for (var cell : row){
                diseaseProcess.simulateDayAtSingleCell(cell);
            }
        }
/*        System.out.println("simulated");
        System.out.println(MapData.getCellAtIndex(new Pair<>(10,10)).getStateCountMap());
        System.out.println(MapData.getCellAtIndex(new Pair<>(10,10)).getImmigrants());*/
        for (var row : MapData.getGridMap()){
            for (var cell : row){
                diseaseProcess.makeMoveBack(cell);
            }
        }
/*        System.out.println("back home");
        System.out.println(MapData.getCellAtIndex(new Pair<>(10,10)).getStateCountMap());
        System.out.println(MapData.getCellAtIndex(new Pair<>(10,10)).getImmigrants());*/
    }
}


