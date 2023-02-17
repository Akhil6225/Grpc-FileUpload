import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AllClients {

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(2);
//        Runnable obj = new NewThread();
        Runnable obj2 = new RunnableClient2();
        List<Runnable> tasks = new ArrayList<>();
//        tasks.add(obj);
        tasks.add(obj2);
            for (int i = 0; i < 2; i++) {
                executor.execute(tasks.get(i));
            }
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            executor.shutdown();
        }
    }

