package pl.agh.kis.seirsimulation.model;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import pl.agh.kis.seirsimulation.model.data.DataLoader;

import java.io.IOException;

@Slf4j
public class Simulation {

    @Autowired DataLoader dataLoader;

    public void run() throws IOException {
        dataLoader.mapData();
        log.debug("sim running");
    }
}
