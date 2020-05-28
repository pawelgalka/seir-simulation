package pl.agh.kis.seirsimulation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import pl.agh.kis.seirsimulation.controller.GuiContext;
import pl.agh.kis.seirsimulation.model.data.MapData;

import static pl.agh.kis.seirsimulation.model.configuration.Configuration.*;

@Slf4j
@NoArgsConstructor
@Getter
public class Simulation {

    @Autowired
    GuiContext context;

    @Autowired
    DiseaseProcess diseaseProcess;

    @Autowired
    Movement movementProcess;

    public void step() {
        log.debug("stepping");
        context.dayStep();
        movementProcess.makeMove();
        for (var row : MapData.getGridMap()){
            for (var cell : row) {
                diseaseProcess.simulateDayAtSingleCell(cell);
            }
        }
        for (var row : MapData.getGridMap()){
            for (var cell : row){
                movementProcess.makeMoveBack(cell);
            }
        }
        if (context.getDistancingLevelChange() != null) {
            context.setDistancingLevel(context.getDistancingLevelChange());
            context.setDistancingLevelChange(null);
            log.debug("CHANGED");
        }

    }

    public void updateMortality() {
        if(MapData.getNumberOfStateSummary(State.I)*HOSPITALIZATION_PERC>MAX_HOSPITAL){
            DISEASE_CONFIG.setMortality(BASE_MORTALITY*2);
            // TODO: 13.05.2020 implement better formula
        }
        else if(MapData.getNumberOfStateSummary(State.I)*HOSPITALIZATION_PERC<=MAX_HOSPITAL){
            DISEASE_CONFIG.setMortality(BASE_MORTALITY);
        }
    }
}


