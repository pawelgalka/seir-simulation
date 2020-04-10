package pl.agh.kis.seirsimulation.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.agh.kis.seirsimulation.model.strategy.DiseaseStrategy;

@Component
public class DiseaseProcess {

    @Autowired DiseaseStrategy diseaseStrategy;

    public void test(){
        System.out.println(diseaseStrategy);
    }
}
