package pl.agh.kis.seirsimulation.model;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pl.agh.kis.seirsimulation.model.configuration.Configuration.*;

@Data
public class Cell {
    private List<Integer> stateCountMap;
    private Integer peopleLimit;

    public Cell(Integer initCount) {
        stateCountMap = IntStream.range(1, LATENCY).mapToObj(x -> 0).collect(Collectors.toList());
        stateCountMap.set(0, initCount);
        peopleLimit = 2 * initCount;
    }

    public void calculateBirthChange() {
        double demographicRate=Math.round(1 + BIRTH_RATE - DEATH_RATE);
        double deathRate=Math.round(1 + BIRTH_RATE - DEATH_RATE - VIRUS_MORTABILITY);
        //S
        stateCountMap.set(0, (int)  demographicRate* stateCountMap.get(0));
        //E
        double exposedRate;
        if(DEADLY_WITHOUT_SYMPTOMS){
            exposedRate=deathRate;
        }else exposedRate=demographicRate;

        for (var i = 1; i <= EXPOSED_TIME + 1; i++) {
            stateCountMap.set(i, (int) exposedRate * stateCountMap.get(i));
        }
        //I
        for (var i = 2 + EXPOSED_TIME; i <= LATENCY - 2; i++) {
            stateCountMap
                    .set(i, (int) deathRate * stateCountMap.get(i));
        }
        //R
        stateCountMap.set(LATENCY - 1, (int) demographicRate * stateCountMap.get(stateCountMap.size()));
    }

    public void calculateInfections(List<Cell> immigrants){

    }
    public void changePositionStateCountMap(){
        for(var i=stateCountMap.size()-1;i>0;i--){
            stateCountMap.set(i+1,stateCountMap.get(i+1)+stateCountMap.get(i));
            stateCountMap.set(i,0);
        }
    }
}
