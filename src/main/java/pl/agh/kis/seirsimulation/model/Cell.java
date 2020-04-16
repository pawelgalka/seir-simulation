package pl.agh.kis.seirsimulation.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
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
    private List<List<Integer>> immigrants;

    public Cell(Integer initCount) {
        stateCountMap = IntStream.range(0, 4).mapToObj(x -> 0).collect(Collectors.toList());
        stateCountMap.set(0, initCount);
        peopleLimit = 2 * initCount;
        D = 0;
        immigrants= new ArrayList<>(Collections.nCopies(8, Collections.nCopies(4, 0)));
    }



    public int[] getImmigrantsSummed(){
        int[]sum={0,0,0,0};
        for(List<Integer>list:immigrants){
            for(int i=0;i<4;i++) {
                sum[i] += list.get(i);
            }
        }
        return sum;
    }
    public List<Integer> getMigratedStateCountMap(){
        int[]immigrated=getImmigrantsSummed();
        List<Integer> MigratedSCM=new ArrayList<>();
        for(int i=0;i<getStateCountMap().size();i++){
            MigratedSCM.add(getStateCountMap().get(i)+immigrated[i]);
        }

        return MigratedSCM;
    }

}
