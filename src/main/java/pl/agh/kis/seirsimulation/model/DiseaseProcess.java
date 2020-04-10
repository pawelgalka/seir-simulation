package pl.agh.kis.seirsimulation.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.model.strategy.DiseaseStrategy;
import java.util.List;

@Component
public class DiseaseProcess {

    @Autowired DiseaseStrategy diseaseStrategy;

    public void test(){
        System.out.println(diseaseStrategy);
        Cell cell=new Cell(28000);
        cell.getStateCountMap().set(1,10);
        for(int i=0;i<100;i++) {
            simulateDayAtSingleCell(cell);
        }
    }
    public void simulateDayAtSingleCell(Cell cell){
        List<Integer> stateCountMap=cell.getStateCountMap();
        int [] changes=diseaseStrategy.getDailyChanges(stateCountMap, stateCountMap.stream().mapToInt(Integer::intValue).sum());
        cell.setD(cell.getD() + diseaseStrategy.calculateDiseaseDeaths(cell.getStateCountMap()));
        for(int j=0;j<stateCountMap.size();j++){
            stateCountMap.set(j,stateCountMap.get(j)+changes[j]);
        }
        cell.setStateCountMap(stateCountMap);
        System.out.println(cell.getStateCountMap());
        System.out.println(cell.getD());
    }
}
