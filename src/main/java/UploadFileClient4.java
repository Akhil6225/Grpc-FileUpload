import com.google.protobuf.ByteString;
import com.akhil.test.ImageUploadGrpc;
import com.akhil.test.PutRequest;
import com.akhil.test.PutResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UploadFileClient4 implements Runnable{
    private static final Logger logger = Logger.getLogger(UploadFileClient.class.getName());
    private static final int PORT = 50054;

    private  ManagedChannel Channel;
    private  ImageUploadGrpc.ImageUploadBlockingStub BlockingStub;
    private  ImageUploadGrpc.ImageUploadStub AsyncStub;

    String location;

    public UploadFileClient4(String host, int port,String s) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build());
        this.location =s;
    }

    UploadFileClient4(ManagedChannel channel) {
        this.Channel = channel;
        BlockingStub = ImageUploadGrpc.newBlockingStub(channel);
        AsyncStub = ImageUploadGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        Channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void startStream(final String filepath) {
        logger.info("tid: " +  Thread.currentThread().isAlive() + ", Will try to getBlob");
//        System.out.println("tid: " +  Thread.currentThread().isAlive() + ", Will try to getBlob");
        StreamObserver<PutResponse> responseObserver = new StreamObserver<PutResponse>() {

            @Override
            public void onNext(PutResponse value) {
                logger.info("Client response onNext");
            }

            @Override
            public void onError(Throwable t) {
                logger.info("Client response onError");
            }

            @Override
            public void onCompleted() {
                logger.info("Client response onCompleted");
            }
        };
        StreamObserver<PutRequest> requestObserver = AsyncStub.getBlob(responseObserver);
        try {

            File file = new File(filepath);
            if (file.exists() == false) {
                logger.info("File does not exist");
                return;
            }
            try {
                BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(file));
                int bufferSize = 512 * 1024; // 512k
                byte[] buffer = new byte[bufferSize];
                int size = 0;
                while ((size = bInputStream.read(buffer)) > 0) {
                    ByteString byteString = ByteString.copyFrom(buffer, 0, size);
                    PutRequest req = PutRequest.newBuilder().setName(filepath).setData(byteString).setOffset(size).build();
                    requestObserver.onNext(req);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }
        requestObserver.onCompleted();
    }

//    public static void main(String[] args) throws Exception {
//        UploadFileClient4 client4 = new UploadFileClient4("localhost", PORT);
//
//        try {
//            Long time = System.currentTimeMillis();
//            client4.startStream("/Users/akhil-pt6225/Downloads/test1.jpg");
//            Long time1 = System.currentTimeMillis()-time;
//            logger.info("Time taken for streaming is " +time1);
//            logger.info("Done with startStream");
//        } finally {
//            client4.shutdown();
//
//        }
//
//    }

    @Override
    public void run() {
        Logger logger1 = Logger.getLogger("ImageLogs");

        FileHandler fh;
        try {
            fh = new FileHandler("/Users/akhil-pt6225/Downloads/ImageLogs.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger1.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        long time = System.currentTimeMillis();
        startStream(location);
        long size;
        try {
            size = Files.size(Paths.get(location));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Long time1 = System.currentTimeMillis()-time;
        logger1.info("Time taken" + " "+ time1 + " " + location + " " + size/1024+ "kb");
        logger.info("Done with startStream");
        try {
            shutdown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}