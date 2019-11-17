package main.problem.bathroom.unisex;

import java.util.concurrent.Semaphore;

public class BathroomUnisexSemaphores implements IBathroomUnisex {

    Semaphore empty = new Semaphore(1);

    Semaphore maleMutex = new Semaphore(1);
    Semaphore male; // Inicializado no construtor

    Semaphore femaleMutex = new Semaphore(1);
    Semaphore female; // Inicializado no construtor

    Semaphore order = new Semaphore(1, true);

    int femaleCount = 0;
    int maleCount = 0;

    public BathroomUnisexSemaphores() {
        this(3);
    }

    public BathroomUnisexSemaphores(int limit) {
        female = new Semaphore(limit);
        male = new Semaphore(limit);
    }

    public void enterBathroom(Person person) {
        try {
            order.acquire();
            switch (person.getSex()) {
            case FEMALE:
                femaleMutex.acquire();

                if (femaleCount == 0)
                    empty.acquire();
                femaleCount++;

                femaleMutex.release();

                female.acquire();
                break;
            case MALE:
                maleMutex.acquire();

                if (maleCount == 0)
                    empty.acquire();
                maleCount++;

                maleMutex.release();

                male.acquire();
                break;
            }
            order.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exitBathroom(Person person) {
        try {
            switch (person.getSex()) {
            case FEMALE:
                female.release();

                femaleMutex.acquire();
                femaleCount--;
                if (femaleCount == 0)
                    empty.release();

                femaleMutex.release();
                break;
            case MALE:
                male.release();

                maleMutex.acquire();
                maleCount--;
                if (maleCount == 0)
                    empty.release();

                maleMutex.release();
                break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
