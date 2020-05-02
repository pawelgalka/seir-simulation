package pl.agh.kis.seirsimulation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import pl.agh.kis.seirsimulation.controller.GuiContext;
import pl.agh.kis.seirsimulation.model.data.MapData;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
        diseaseProcess.makeMove();
        for (var row : MapData.getGridMap()){
            for (var cell : row){
                diseaseProcess.simulateDayAtSingleCell(cell);
            }
        }
        for (var row : MapData.getGridMap()){
            for (var cell : row){
                diseaseProcess.makeMoveBack(cell);
            }
        }
    }
}


