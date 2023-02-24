import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunnableClient implements Runnable {
//    public static void main(String[] args) throws Exception {
//        String[] fileLocation = new String[10];
//        fileLocation[0] = "/Users/akhil-pt6225/Downloads/TEST.jpg";
//        fileLocation[1] = "/Users/akhil-pt6225/Downloads/test10.jpg";
//        fileLocation[2] = "/Users/akhil-pt6225/Downloads/test3.jpg";
//        fileLocation[3] = "/Users/akhil-pt6225/Downloads/test5.jpg";
//        fileLocation[4] = "/Users/akhil-pt6225/Downloads/test6.jpg";
//        fileLocation[5] = "/Users/akhil-pt6225/Downloads/test7.jpg";
//        fileLocation[6] = "/Users/akhil-pt6225/Downloads/test21.jpg";
//        fileLocation[7] = "/Users/akhil-pt6225/Downloads/test13.jpg";
//        fileLocation[8] = "/Users/akhil-pt6225/Downloads/test14.jpg";
//        fileLocation[9] = "/Users/akhil-pt6225/Downloads/test20.jpg";
//        CountDownLatch latch = new CountDownLatch(10);
//        ExecutorService executor = Executors.newFixedThreadPool(4);
////        long time = System.currentTimeMillis();
//        for (int i = 0; i < 10; i++) {
//            Runnable client = new UploadFileClient("localhost", 50055, fileLocation[i],latch);
//            executor.execute(client);
//        }
////        long time1 = System.currentTimeMillis() - time;
////          System.out.println("Total Time Taken :" + " " + time1);
//        latch.await();
//        executor.shutdown();
//        }


//    RunnableClient(CountDownLatch latch){
//        this.latch = latch;
//    }
    @Override
    public void run() {
            String[] fileLocation = new String[5];
            fileLocation[0] = "/Users/akhil-pt6225/Downloads/test20.jpg";
            fileLocation[1] = "/Users/akhil-pt6225/Downloads/test10.jpg";
            fileLocation[2] = "/Users/akhil-pt6225/Downloads/test21.jpg";
            fileLocation[3] = "/Users/akhil-pt6225/Downloads/test5.jpg";
            fileLocation[4] = "/Users/akhil-pt6225/Downloads/test14.jpg";
////        fileLocation[5] = "/Users/akhil-pt6225/Downloads/test7.jpg";
////        fileLocation[6] = "/Users/akhil-pt6225/Downloads/test21.jpg";
////        fileLocation[7] = "/Users/akhil-pt6225/Downloads/test13.jpg";
////        fileLocation[8] = "/Users/akhil-pt6225/Downloads/test14.jpg";
////        fileLocation[9] = "/Users/akhil-pt6225/Downloads/test20.jpg";
            CountDownLatch latch = new CountDownLatch(5);
            ExecutorService executor = Executors.newFixedThreadPool(2);
//        long time = System.currentTimeMillis();
            for (int i = 0; i < 5; i++) {
                Runnable client = new UploadFileClient("localhost", 50055, fileLocation[i], latch);
                executor.execute(client);
            }
//        long time1 = System.currentTimeMillis() - time;
//          System.out.println("Total Time Taken :" + " " + time1);
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executor.shutdown();
    }
}
//    }
//    @Override
//    public void run() {
//
//
//        String[] fileLocation = new String[10];
//
//        fileLocation[0] = "/Users/akhil-pt6225/Downloads/TEST.jpg";
//        fileLocation[1] = "/Users/akhil-pt6225/Downloads/test7.jpg";
//        fileLocation[2] = "/Users/akhil-pt6225/Downloads/test6.jpg";
//        fileLocation[3] = "/Users/akhil-pt6225/Downloads/test3.jpg";
//        fileLocation[4] = "/Users/akhil-pt6225/Downloads/test7.jpg";
//        fileLocation[5] = "/Users/akhil-pt6225/Downloads/test10.jpg";
//        fileLocation[6] = "/Users/akhil-pt6225/Downloads/test11.jpg";
//        fileLocation[7] = "/Users/akhil-pt6225/Downloads/test13.jpg";
//        fileLocation[8] = "/Users/akhil-pt6225/Downloads/test14.jpg";
//        fileLocation[9] = "/Users/akhil-pt6225/Downloads/test.png";
//
//        ExecutorService executor = Executors.newFixedThreadPool(4);
//        long time = System.currentTimeMillis();
//        for (int i = 0; i < fileLocation.length; i++) {
//            Runnable obj = new UploadFileClient("localhost", 50054, fileLocation[i]);
//            executor.execute(obj);
//            try {
//                executor.awaitTermination(500, TimeUnit.MILLISECONDS);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

//        long time1 = System.currentTimeMillis() - time;
//      //  System.out.println("Total Time Taken :" + " " + time1);
//        executor.shutdown();
//        try {
//            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
//                executor.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            executor.shutdownNow();
//        }
//    }



