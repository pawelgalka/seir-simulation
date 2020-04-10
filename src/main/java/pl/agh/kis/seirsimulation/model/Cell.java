package pl.agh.kis.seirsimulation.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import pl.agh.kis.seirsimulation.model.strategy.DiseaseStrategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pl.agh.kis.seirsimulation.model.configuration.Configuration.*;

@Data
public class Cell {
    private List<Integer> stateCountMap;
    private Integer peopleLimit;
    private Integer S;
    private Integer E;
    private Integer I;
    private Integer R;
    private Integer N;
    private Integer D;

    @Autowired
    public DiseaseStrategy diseaseStrategy;

    public Cell(Integer initCount) {
        stateCountMap = IntStream.range(0, 4).mapToObj(x -> 0).collect(Collectors.toList());
        stateCountMap.set(0, initCount);
        peopleLimit = 2 * initCount;
        D = 0;
    }

    public void simulateDay(){
        updateSEIRstats();
        var dD=diseaseStrategy.calculateDiseaseDeaths(stateCountMap);
        int[]changes=diseaseStrategy.getDailyChanges(stateCountMap,N);

        for(int i=0;i<stateCountMap.size();i++){
            stateCountMap.set(i,stateCountMap.get(i)+changes[i]);
        }
        D+=dD;
    }

    public void updateSEIRstats(){
        S=stateCountMap.get(0);
        E=stateCountMap.get(1);
        I=stateCountMap.get(2);
        R=stateCountMap.get(3);
        N=S+E+I+R;
        System.out.println(stateCountMap);
        System.out.println(D);
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
