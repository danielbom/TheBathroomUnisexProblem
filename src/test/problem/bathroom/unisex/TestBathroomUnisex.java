package test.problem.bathroom.unisex;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import main.problem.bathroom.unisex.BathroomUnisexMonitor;
import main.problem.bathroom.unisex.BathroomUnisexSemaphores;
import main.problem.bathroom.unisex.IBathroomUnisex;
import main.problem.bathroom.unisex.Person;
import main.problem.bathroom.unisex.Sex;

public class TestBathroomUnisex implements Runnable {

    private static ArrayList<Person> peoples = new ArrayList<Person>();
    private static int limitOfBathroom = 3;

    private IBathroomUnisex bath;
    private Person person;

    public TestBathroomUnisex(IBathroomUnisex bath, Person person) {
        this.bath = bath;
        this.person = person;
    }

    @Override
    public void run() {
        String name = Thread.currentThread()
                            .getName();
        try {
            // Registrando a ordem de criação de cada pessoa
            System.out.println("Person: " + person + " created.");
            Thread.sleep(200);

            // Entrando no banheiro
            bath.enterBathroom(person);
            System.out.println(name + " << ENTER >> " + person + " using bathroom: " + person.getTime());

            // Usando o banheiro
            person.useBathroom();

            // Saindo do banheiro
            System.out.println(name + " << EXIT >> " + person + " using bathroom: " + person.getTime());
            bath.exitBathroom(person);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void controlledTest(IBathroomUnisex bath, String bname) throws InterruptedException {
        long startTime = System.nanoTime();
        System.out.println(bname + "\n");

        ExecutorService executor = Executors.newCachedThreadPool();
        peoples.forEach(person -> {
            executor.submit(new TestBathroomUnisex(bath, person));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);

        System.out.println();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Time: " + duration / 1e9);
        System.out.println();
    }

    // Basic Test Case
    public static void basicTestCase() {
        randomSexPeopleTestCase(30, 3);
    }

    // Random Sex People Test Case
    public static void randomSexPeopleTestCase(int numberOfPeoples, int limit) {
        limitOfBathroom = limit;
        for (int i = 0; i < numberOfPeoples; i++) {
            Sex sex = (Math.random() > 0.5 ? Sex.FEMALE : Sex.MALE);
            long time = (long) (Math.random() * 3000 + 1000);
            peoples.add(new Person(sex, time));
        }
    }

    // Consecutive Same Sex Test Case
    public static void consecutiveSameSexTestCase(int numberOfPeoples, int limit, int time) {
        limitOfBathroom = limit;
        int interval = (int) Math.floor(numberOfPeoples / limit);
        for (int i = 0; i < interval; i++)
            for (int j = 0; j < limit; j++)
                peoples.add(new Person(i % 2 == 0 ? Sex.MALE : Sex.FEMALE, time));
    }

    // Intercalated Sex Test Case
    public static void intercalatedSexTestCase(int numberOfPeoples, int limit, int time) {
        limitOfBathroom = limit;
        for (int i = 0; i < numberOfPeoples / 2; i++) {
            peoples.add(new Person(Sex.MALE, time));
            peoples.add(new Person(Sex.FEMALE, time));
        }
    }

    // Two By One Proportion Test Case
    public static void twoByOneProportionTestCase(int numberOfPeoples, int limit, int time) {
        limitOfBathroom = limit;
        for (int i = 0; i < (numberOfPeoples / (3 * limit)) + 1; i++)
            for (int j = 0; j < limit; j++)
                peoples.add(new Person(i % 3 == 0 ? Sex.MALE : Sex.FEMALE, time));
    }

    public static void main(String[] args) throws InterruptedException {
        basicTestCase();
        controlledTest(new BathroomUnisexMonitor(limitOfBathroom), "Bath Monitor");
        controlledTest(new BathroomUnisexSemaphores(limitOfBathroom), "Bath Semaphores");
    }
}
