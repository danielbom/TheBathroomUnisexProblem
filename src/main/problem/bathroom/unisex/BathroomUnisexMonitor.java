package main.problem.bathroom.unisex;

public class BathroomUnisexMonitor implements IBathroomUnisex {

    int women = 0;
    int men = 0;

    int menWaiting = 0;
    int womenWaiting = 0;

    Sex priority = Sex.FEMALE;

    int consecutive = 0;
    int maxConsecutive; // Inicializado no construtor

    public BathroomUnisexMonitor() {
        this(3);
    }

    public BathroomUnisexMonitor(int limit) {
        maxConsecutive = limit;
    }

    private void changePriority(Sex sex) {
        priority = sex;
        consecutive = 0;
    }

    @Override
    public synchronized void enterBathroom(Person person) {
        int limit = maxConsecutive - 1;
        try {
            switch (person.getSex()) {
            case FEMALE:
                womenWaiting++;
                while (priority == Sex.MALE || women > limit || men != 0) {
                    if (menWaiting == 0 && women <= limit && men == 0) {
                        changePriority(Sex.FEMALE);
                        break;
                    }
                    this.wait();
                }
                womenWaiting--;

                women++;
                consecutive++;

                if (consecutive >= maxConsecutive)
                    changePriority(Sex.MALE);
                break;
            case MALE:
                menWaiting++;
                while (priority == Sex.FEMALE || men > limit || women != 0) {
                    if (womenWaiting == 0 && men <= limit && women == 0) {
                        changePriority(Sex.MALE);
                        break;
                    }
                    this.wait();
                }
                menWaiting--;

                men++;
                consecutive++;

                if (consecutive == maxConsecutive)
                    changePriority(Sex.FEMALE);
                break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void exitBathroom(Person person) {
        switch (person.getSex()) {
        case FEMALE:
            women--;
            if (women == 0)
                this.notifyAll();
            break;
        case MALE:
            men--;
            if (men == 0)
                this.notifyAll();
            break;
        }
    }

    @Override
    public String toString() {
        return String.format("BathroomUnisex(w: %d, m: %d, ww: %d, mw: %d, c: %d, p: %d)", women, men, womenWaiting,
                menWaiting, consecutive, priority);
    }
}
