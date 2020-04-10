package pl.agh.kis.seirsimulation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Getter
public class Simulation {

    private boolean isRunning;

    public void run() {
        log.debug("sim running");
        
        isRunning = true;
    }

    public void pause() {
        log.debug("sim paused");
        isRunning = false;
    }

    public void step() {
        log.debug("stepping");
    }
}
