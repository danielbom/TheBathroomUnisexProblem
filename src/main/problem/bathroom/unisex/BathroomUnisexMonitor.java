package main.problem.bathroom.unisex;

public class BathroomUnisexMonitor implements IBathroomUnisex {

    int womans = 0;
    int mans = 0;

    int mansWaiting = 0;
    int womansWaiting = 0;

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
                womansWaiting++;

                while (priority == Sex.MALE || womans > 2 || mans != 0) {
                    if (mansWaiting == 0 && womans <= 2 && mans == 0) {
                        changePriority(Sex.FEMALE);
                        break;
                    }
                    this.wait();
                }

                womans++;
                consecutive++;

                if (consecutive >= maxConsecutive) changePriority(Sex.MALE);

                womansWaiting--;
                break;
            case MALE:
                mansWaiting++;

                while (priority == Sex.FEMALE || mans > limit || womans != 0) {
                    if (womansWaiting == 0 && mans <= limit && womans == 0) {
                        changePriority(Sex.MALE);
                        break;
                    } else {
                        this.wait();
                    }
                }

                mans++;
                consecutive++;

                if (consecutive == maxConsecutive) changePriority(Sex.FEMALE);

                mansWaiting--;
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
            womans--;
            break;
        case MALE:
            mans--;
            break;
        }
        if (mans == 0 || womans == 0) this.notifyAll();
    }

    @Override
    public String toString() {
        return "BathroomUnisex(" + "w: " + womans + " m: " + mans + " ww: " + womansWaiting + " mw: " + mansWaiting +
                " c: " + consecutive + " p: " + priority + ")";
    }
}
