package pl.agh.kis.seirsimulation.model;

import lombok.Data;

@Data
public class Person {
    State personState;

    public Person(){
        personState = State.S;
    }
}
