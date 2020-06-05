package pl.agh.kis.seirsimulation.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@NoArgsConstructor
@Component
public class Cell {
    private List<Integer> stateCountMap;
    private Integer peopleLimit;
    private Map<Pair<Integer,Integer>, List<Integer>> immigrants;
    public Cell(Integer initCount) {
        stateCountMap = IntStream.range(0, 5).mapToObj(x -> 0).collect(Collectors.toList());
        stateCountMap.set(0, initCount);
        peopleLimit = 2 * initCount;
        immigrants= new HashMap<>();
    }



    public int[] getImmigrantsSummed(){
        int[]sum={0,0,0,0,0};
        immigrants.forEach((index,list)->{
            for(int i=0;i<sum.length-1;i++) {
                sum[i] += list.get(i);
            }
        });
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

    public static final IntPredicate isSick = index -> 2 >= index && index >= 1;

}
