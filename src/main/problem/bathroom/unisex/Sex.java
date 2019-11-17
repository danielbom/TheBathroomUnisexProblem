package main.problem.bathroom.unisex;

public enum Sex {
    MALE("Male"), FEMALE("Female");

    private final String name;

    private Sex(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
