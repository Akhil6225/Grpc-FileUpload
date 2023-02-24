import com.google.protobuf.ByteString;
import com.akhil.test.ImageUploadGrpc;
import com.akhil.test.PutRequest;
import com.akhil.test.PutResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UploadFileClient2 implements  Runnable {
    private static final Logger logger = Logger.getLogger(UploadFileClient.class.getName());
    private static final int PORT = 50055;

    private ManagedChannel Channel;
    private ImageUploadGrpc.ImageUploadBlockingStub BlockingStub;
    private ImageUploadGrpc.ImageUploadStub AsyncStub;
    CountDownLatch latch;
    private String location;

    public UploadFileClient2(String host, int port, String loc,CountDownLatch latch) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .maxInboundMessageSize(999999999)
                .usePlaintext()
                .build());
        this.location = loc;
        this.latch=latch;
    }

    UploadFileClient2(ManagedChannel channel) {
        this.Channel = channel;
        BlockingStub = ImageUploadGrpc.newBlockingStub(channel);
        AsyncStub = ImageUploadGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        Channel.shutdown();
    }

    public void startStream(final String filepath) throws InterruptedException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        logger.info("tid: " + Thread.currentThread().getId() + ", Will try to getBlob");
        StreamObserver<PutResponse> responseObserver = new StreamObserver<PutResponse>() {

            @Override
            public void onNext(PutResponse value) {
                logger.info("Client response onNext");
            }

            @Override
            public void onError(Throwable t) {
                logger.info("Client response onError");
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Client response onCompleted");
                countDownLatch.countDown();
            }
        };
        if(!countDownLatch.await(2000, TimeUnit.MILLISECONDS)){
            logger.info("Time exceeded 1 second");
        }
        StreamObserver<PutRequest> requestObserver = AsyncStub.withDeadlineAfter(1,TimeUnit.SECONDS).getBlob(responseObserver);
        try {

            File file = new File(filepath);
            if (!file.exists()) {
                logger.info("File does not exist");
                return;
            }
            try {
                BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(file));
                int bufferSize = 15 * 1024 * 1024;
                byte[] buffer = new byte[bufferSize];
                int size = 0;
                while ((size = bInputStream.read(buffer)) > 0) {
                    ByteString byteString = ByteString.copyFrom(buffer, 0, size);
                    PutRequest req = PutRequest.newBuilder().setName(filepath).setData(byteString).setOffset(size).build();
                    requestObserver.onNext(req);
                }
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
//        UploadFileClient2 client2 = new UploadFileClient2("localhost", PORT);
//        try {
//            Long time = System.currentTimeMillis();
//            client2.startStream("/Users/akhil-pt6225/Downloads/test5.jpg");
//            Long time1 = System.currentTimeMillis()-time;
//            logger.info("Time taken for streaming is " +time1);
//            logger.info("Done with startStream");
//        } finally {
//            client2.shutdown();
//        }
//    }

    @Override
    public void run() {
          try {
            System.out.println(Thread.currentThread().getId() + " started");
            startStream(location);
            logger.info("Done with startStream");
            System.out.println(Thread.currentThread().getId() + " ended");
            shutdown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            latch.countDown();
        }
    }
}