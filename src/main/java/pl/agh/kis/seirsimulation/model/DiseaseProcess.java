package pl.agh.kis.seirsimulation.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.model.strategy.DiseaseStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static pl.agh.kis.seirsimulation.model.configuration.Configuration.MOVING_PPL_PERC;
import static pl.agh.kis.seirsimulation.model.configuration.Configuration.floorOrCeil;

@Component
public class DiseaseProcess {

    @Autowired DiseaseStrategy diseaseStrategy;

    public void test(){
        Cell cell=new Cell(28000);
        cell.getStateCountMap().set(1,10);

        Cell cell1=new Cell(25000);
        cell1.getStateCountMap().set(1,10);

        Cell cell2=new Cell(26000);
        cell2.getStateCountMap().set(1,10);

        Cell cell3=new Cell(27000);
        cell3.getStateCountMap().set(1,10);

        Cell cell4=new Cell(22000);

        Cell cell5=new Cell(29000);
        cell5.getStateCountMap().set(1,10);

        Cell cell6=new Cell(38000);
        cell6.getStateCountMap().set(1,10);

        Cell cell7=new Cell(18000);
        cell7.getStateCountMap().set(1,10);
        cell7.getStateCountMap().set(2,10);

        Cell cell8=new Cell(15000);

        Cell cell9=new Cell(18000);
        cell9.getStateCountMap().set(1,10);

        Cell cell10=new Cell(18000);
        cell10.getStateCountMap().set(1,10);

        Cell cell11=new Cell(18000);
        cell11.getStateCountMap().set(1,10);


        List<Cell> neighbourhood=new ArrayList<>();
        neighbourhood.add(cell);
        neighbourhood.add(cell1);
        neighbourhood.add(cell2);
        neighbourhood.add(cell3);
        neighbourhood.add(cell5);
        neighbourhood.add(cell6);
        neighbourhood.add(cell7);
        neighbourhood.add(cell8);
        //cell4 source

        List<Cell> neighbourhood2=new ArrayList<>();
        neighbourhood2.add(cell3);
        neighbourhood2.add(cell4);
        neighbourhood2.add(cell5);
        neighbourhood2.add(cell6);
        neighbourhood2.add(cell8);
        neighbourhood2.add(cell9);
        neighbourhood2.add(cell10);
        neighbourhood2.add(cell11);
        //cell7 source
        System.out.println("cell4 "+ cell4.getStateCountMap());
        System.out.println("cell7" +cell7.getStateCountMap());
        makeMove(cell4,neighbourhood);
        System.out.println("cell4 "+ cell4.getStateCountMap());
        System.out.println("cell7" +cell7.getStateCountMap());
        System.out.println(cell7.getImmigrants());
        simulateDayAtSingleCell(cell7);
        System.out.println("cell4 "+ cell4.getStateCountMap());
        System.out.println("cell7" +cell7.getStateCountMap());
        makeMoveBack(cell7,neighbourhood2);
        System.out.println(cell7.getImmigrants());
        System.out.println("cell4 "+ cell4.getStateCountMap());
        System.out.println("cell7" +cell7.getStateCountMap());
    }

    public void simulateDayAtSingleCell(Cell cell){
        List<Integer> stateCountMap=cell.getMigratedStateCountMap();
        System.out.println("scm"+stateCountMap);
        int [] changes=diseaseStrategy.getDailyChanges(stateCountMap, stateCountMap.stream().mapToInt(Integer::intValue).sum());
        System.out.println("changes"+ Arrays.toString(changes));
        List<List<Integer>> immigrants=cell.getImmigrants();
        List<Integer> immigrantChangesSum=new ArrayList<>(Collections.nCopies(4,0));
        int cellPeopleSum=stateCountMap.stream().mapToInt(Integer::intValue).sum();
        for(int i=0;i<immigrants.size();i++){
            List<Integer> immigrantsFrom=new ArrayList<>(immigrants.get(i));
            for(int j=0;j<immigrantsFrom.size();j++){
                immigrantsFrom.set(j,immigrantsFrom.get(j)+(int)floorOrCeil((double)(immigrantsFrom.get(j)*changes[j])/cellPeopleSum));
                immigrantChangesSum.set(j,immigrantChangesSum.get(j)+(int)floorOrCeil((double)(immigrantsFrom.get(j)*changes[j])/cellPeopleSum));
                System.out.println((int)floorOrCeil((double)(immigrantsFrom.get(j)*changes[j])/cellPeopleSum));
            }
            immigrants.set(i,immigrantsFrom);
        }
        System.out.println("immigrants"+immigrants);
        cell.setImmigrants(immigrants);
        System.out.println(immigrantChangesSum);
        //TODO manage deaths
        cell.setD(cell.getD() + diseaseStrategy.calculateDiseaseDeaths(cell.getStateCountMap()));
        for(int j=0;j<stateCountMap.size();j++){
            stateCountMap.set(j,stateCountMap.get(j)+changes[j]+immigrantChangesSum.get(j));
        }
        cell.setStateCountMap(stateCountMap);
    }

    public void makeMove(Cell source,List<Cell> neighbours){
        var notEmptyNeighbours=neighbours.stream().filter(e->e.getStateCountMap().stream().mapToInt(Integer::intValue).sum()>0).count();
        List<Integer>moving=getMovingPeople(source, ((int) notEmptyNeighbours));
        Cell cellTmp;
        List<List<Integer>> immigrantsTmp;
        for(int i=0;i<neighbours.size();i++){
            cellTmp=neighbours.get(i);
            if(cellTmp.getStateCountMap().stream().mapToInt(Integer::intValue).sum()>0) {
                immigrantsTmp = neighbours.get(i).getImmigrants();
                immigrantsTmp.set(neighbours.size() - i - 1, moving);
                cellTmp.setImmigrants(immigrantsTmp);
                neighbours.set(i, cellTmp);
            }
        }
        List<Integer> sourceCp=new ArrayList<>();
        for(Integer num:source.getStateCountMap()){
            sourceCp.add((int)floorOrCeil(num*(1-MOVING_PPL_PERC)));
        }
        source.setStateCountMap(sourceCp);
    }

    public List<Integer> getMovingPeople(Cell c,int notEmptyNeighbours){
         return c.getStateCountMap().stream().map(e->(int)floorOrCeil(e*MOVING_PPL_PERC/notEmptyNeighbours)).collect(Collectors.toList());
    }
    public void makeMoveBack(Cell source,List<Cell> neighbours){
        List<Integer> sourceSCM=source.getStateCountMap();
        List<Integer> immigrantsFrom;
        for(int i=0;i<neighbours.size();i++){
            immigrantsFrom= new ArrayList<>(source.getImmigrants().get(i));
            Cell neighbour=neighbours.get(i);
            List<Integer> neighbourSCM=neighbour.getStateCountMap();
            for(int j=0;j<immigrantsFrom.size();j++){
                sourceSCM.set(j,sourceSCM.get(j)-immigrantsFrom.get(j));
                neighbourSCM.set(j,neighbourSCM.get(j)+immigrantsFrom.get(j));
                immigrantsFrom.set(j,0);
            }
            neighbour.setStateCountMap(neighbourSCM);
            neighbours.set(i,neighbour);
        }
        source.setStateCountMap(sourceSCM);
        source.setImmigrants(Collections.nCopies(8,Collections.nCopies(4,0)));
    }
}
