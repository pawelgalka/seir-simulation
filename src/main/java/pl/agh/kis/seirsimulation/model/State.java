package pl.agh.kis.seirsimulation.model;

public enum State {
    S(0), E(1), I(2), R(3), D(4);

    private final int state;

    State(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
