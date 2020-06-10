package pl.agh.kis.seirsimulation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pl.agh.kis.seirsimulation.controller.GuiContext;
import pl.agh.kis.seirsimulation.model.configuration.disease.DiseaseConfig;
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
        context.setNotChanging(true);
        context.dayStep();
        movementProcess.makeMove();
        for (var row : MapData.getGridMap()) {
            for (var cell : row) {
                diseaseProcess.simulateDayAtSingleCell(cell);
            }
        }
        for (var row : MapData.getGridMap()) {
            for (var cell : row) {
                movementProcess.makeMoveBack(cell);
            }
        }
        if (context.getDistancingLevelChange() != null) {
            context.setDistancingLevel(context.getDistancingLevelChange());
            if (DISEASE_CONFIG.getReproduction() == DiseaseConfig
                    .retrieveReproductionBasedOnDistancingLevel(DISEASE_CONFIG, context.getDistancingLevel())) {
                context.setDistancingLevelChange(null);
                log.debug("CHANGED");
                DiseaseConfig.iterator = 0;
            }
            else {
                log.debug("CHANGING ITERATION {}", DiseaseConfig.iterator);
                DiseaseConfig.iterator++;
            }
        }

    }

    public void updateMortality() {
        if (MapData.getNumberOfStateSummary(State.I) * DISEASE_CONFIG.getHospitalizationPerc() > MAX_HOSPITAL) {
            DISEASE_CONFIG.setMortality(
                    BASE_MORTALITY + (MapData.getNumberOfStateSummary(State.I) * DISEASE_CONFIG.getHospitalizationPerc() / 1000000));
        } else if (MapData.getNumberOfStateSummary(State.I) * DISEASE_CONFIG.getHospitalizationPerc() <= MAX_HOSPITAL) {
            DISEASE_CONFIG.setMortality(BASE_MORTALITY);
        }
    }
}


