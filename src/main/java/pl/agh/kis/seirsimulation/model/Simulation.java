package pl.agh.kis.seirsimulation.model;

import static pl.agh.kis.seirsimulation.model.configuration.Configuration.BASE_MORTALITY;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.DISEASE_CONFIG;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.MAX_HOSPITAL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.agh.kis.seirsimulation.controller.GuiContext;
import pl.agh.kis.seirsimulation.model.configuration.disease.DiseaseConfig;
import pl.agh.kis.seirsimulation.model.data.MapData;

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
        for (List<Cell> row : MapData.getGridMap()) {
            for (Cell cell : row) {
                diseaseProcess.simulateDayAtSingleCell(cell);
            }
        }
        for (List<Cell> row : MapData.getGridMap()) {
            for (Cell cell : row) {
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
            } else {
                log.debug("CHANGING ITERATION {}", DiseaseConfig.iterator);
                DiseaseConfig.iterator++;
            }
        }

    }

    public void updateMortality() {
        if (MapData.getNumberOfStateSummary(State.I) * DISEASE_CONFIG.getHospitalizationPerc() > MAX_HOSPITAL) {
            DISEASE_CONFIG.setMortality(
                    BASE_MORTALITY + (MapData.getNumberOfStateSummary(State.I) * DISEASE_CONFIG.getHospitalizationPerc()
                            / 1000000));
        } else if (MapData.getNumberOfStateSummary(State.I) * DISEASE_CONFIG.getHospitalizationPerc() <= MAX_HOSPITAL) {
            DISEASE_CONFIG.setMortality(BASE_MORTALITY);
        }
    }
}


