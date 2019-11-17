package main.problem.bathroom.unisex;

public class Person {
    private Sex sex; // Male or Female
    private long time; // Time spend on bathroom

    public Person(Sex sex, long time) {
        this.sex = sex;
        this.time = time;
    }

    @Override
    public String toString() {
        return sex.toString();
    }

    public void useBathroom() throws InterruptedException {
        Thread.sleep(time);
    }

    public Sex getSex() {
        return sex;
    }

    public long getTime() {
        return time;
    }
}
