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

    private boolean isRunning;

    private Timer timer;

    public void run() {
        log.debug("sim running");
        if(!isRunning){
            isRunning = true;
            timer = new Timer();
            timer.schedule(new EpidemicStep(), 0, 500);
        }
    }

    public void pause() {
        log.debug("sim paused");
        isRunning = false;
        timer.cancel();
    }

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

    class EpidemicStep extends TimerTask {
        public void run() {
           Simulation.this.step();
        }
    }
}


