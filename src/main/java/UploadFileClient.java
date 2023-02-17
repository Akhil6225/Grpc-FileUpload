import com.google.protobuf.ByteString;
import com.akhil.test.ImageUploadGrpc;
import com.akhil.test.PutRequest;
import com.akhil.test.PutResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class UploadFileClient implements Runnable{
    private static final Logger logger = Logger.getLogger(UploadFileClient.class.getName());
    private static final int PORT = 50054;
    private ManagedChannel Channel;
    private ImageUploadGrpc.ImageUploadBlockingStub BlockingStub;
    private ImageUploadGrpc.ImageUploadStub AsyncStub;

    String location;
    CountDownLatch latch;

    public UploadFileClient(String host, int port, String loc, CountDownLatch latch) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .maxInboundMessageSize(999999999)
                .usePlaintext()
                .build());
        this.location= loc;
        this.latch = latch;
    }

    UploadFileClient(ManagedChannel channel) {
        this.Channel = channel;
        BlockingStub = ImageUploadGrpc.newBlockingStub(channel);
        AsyncStub = ImageUploadGrpc.newStub(channel);
    }
    public void shutdown() throws InterruptedException {
        Channel.shutdown().awaitTermination(15, TimeUnit.SECONDS);
    }

    public void startStream(final String filepath) {
        logger.info("tid: " + Thread.currentThread().getId() + ", Will try to getBlob");
        StreamObserver<PutResponse> responseObserver = new StreamObserver<PutResponse>() {

            @Override
            public void onNext(PutResponse value) {
                logger.info("Client response onNext");
                logger.info(value.getStatus() + " - " + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.info("Client response onError :: " + t.getMessage() + Arrays.toString(t.getStackTrace()));
            }

            @Override
            public void onCompleted() {
                logger.info("Client response onCompleted");
            }
        };
        StreamObserver<PutRequest> requestObserver = AsyncStub.getBlob(responseObserver);
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                logger.info("File does not exist");
                return;
            }
            try {
                logger.info("reading file");
                BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(file));
                int bufferSize = 11000 * 1024 ;
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
