package pl.agh.kis.seirsimulation.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@NoArgsConstructor
@Component
public class Cell {
    private List<Integer> stateCountMap;
    private Integer peopleLimit;
    private Integer D;

    public Cell(Integer initCount) {
        stateCountMap = IntStream.range(0, 4).mapToObj(x -> 0).collect(Collectors.toList());
        stateCountMap.set(0, initCount);
        peopleLimit = 2 * initCount;
        D = 0;
    }

    /*    public void calculateBirthChange() {
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
        stateCountMap.set(LATENCY - 1, (int) demographicRate * stateCountMap.get(stateCountMap.size()-1));

        public void changePositionStateCountMap(){
        for(var i=stateCountMap.size()-2;i>0;i--){
            stateCountMap.set(i+1,stateCountMap.get(i+1)+stateCountMap.get(i));
            stateCountMap.set(i,0);
        }

        public int countExposed(){
        return stateCountMap.subList(1,EXPOSED_TIME+2).stream().mapToInt(Integer::intValue).sum();
    }
    public int countInfected(){
        return stateCountMap.subList(EXPOSED_TIME+2,stateCountMap.size()-1).stream().mapToInt(Integer::intValue).sum();
    }*/
}
