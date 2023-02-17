import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunnableClient2 implements Runnable {
  //  public static void main(String[] args) throws Exception {

//        String[] fileLocation = new String[10];
//        fileLocation[0] = "/Users/akhil-pt6225/Downloads/TEST.jpg";
//        fileLocation[1] = "/Users/akhil-pt6225/Downloads/test2.jpg";
//        fileLocation[2] = "/Users/akhil-pt6225/Downloads/test6.jpg";
//        fileLocation[3] = "/Users/akhil-pt6225/Downloads/test3.jpg";
//        fileLocation[4] = "/Users/akhil-pt6225/Downloads/test7.jpg";
//        fileLocation[5] = "/Users/akhil-pt6225/Downloads/test10.jpg";
//        fileLocation[6] = "/Users/akhil-pt6225/Downloads/test11.jpg";
//        fileLocation[7] = "/Users/akhil-pt6225/Downloads/test13.jpg";
//        fileLocation[8] = "/Users/akhil-pt6225/Downloads/test14.jpg";
//        fileLocation[9] = "/Users/akhil-pt6225/Downloads/test.png";
//
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//
//        Logger logger1 = Logger.getLogger("ImageLogs");
//
//        FileHandler fh;
//        try {
//            fh = new FileHandler("/Users/akhil-pt6225/Downloads/ImageLogs.txt");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        logger1.addHandler(fh);
//        SimpleFormatter formatter = new SimpleFormatter();
//        fh.setFormatter(formatter);
//        long time = System.currentTimeMillis();
//
//        for (int i = 0; i < 10; i++) {
//            Runnable obj = new UploadFileClient4("localhost", 50054, fileLocation[i]);
//            executor.submit(obj);
//            try {
//                executor.awaitTermination(500, TimeUnit.MILLISECONDS);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        long time1 = System.currentTimeMillis() - time;
//        logger1.info("Total Time Taken :" + " " + time1 );
//        executor.shutdown();
//    }
//}

    @Override
    public void run() {
        String[] fileLocation = new String[1];
        fileLocation[0] = "/Users/akhil-pt6225/Downloads/TEST.jpg";
//        fileLocation[1] = "/Users/akhil-pt6225/Downloads/test2.jpg";
//        fileLocation[2] = "/Users/akhil-pt6225/Downloads/test6.jpg";
//        fileLocation[3] = "/Users/akhil-pt6225/Downloads/test3.jpg";
//        fileLocation[4] = "/Users/akhil-pt6225/Downloads/test7.jpg";
//        fileLocation[5] = "/Users/akhil-pt6225/Downloads/test10.jpg";
//        fileLocation[6] = "/Users/akhil-pt6225/Downloads/test11.jpg";
//        fileLocation[7] = "/Users/akhil-pt6225/Downloads/test13.jpg";
//        fileLocation[8] = "/Users/akhil-pt6225/Downloads/test14.jpg";
//        fileLocation[9] = "/Users/akhil-pt6225/Downloads/test.png";
        CountDownLatch latch = new CountDownLatch(4);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 1; i++) {
            Runnable obj = new UploadFileClient2("localhost", 50054, fileLocation[i],latch);
            executor.execute(obj);
        }
//        long time1 = System.currentTimeMillis() - time;
//        logger1.info("Total Time Taken :" + " " + time1 );
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();
    }
}




