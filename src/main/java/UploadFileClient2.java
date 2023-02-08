import com.google.protobuf.ByteString;
import com.akhil.test.ImageUploadGrpc;
import com.akhil.test.PutRequest;
import com.akhil.test.PutResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UploadFileClient2 {
    private static final Logger logger = Logger.getLogger(UploadFileClient.class.getName());
    private static final int PORT = 50053;

    private final ManagedChannel Channel;
    private final ImageUploadGrpc.ImageUploadBlockingStub BlockingStub;
    private final ImageUploadGrpc.ImageUploadStub AsyncStub;

    public UploadFileClient2(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build());
    }

    UploadFileClient2(ManagedChannel channel) {
        this.Channel = channel;
        BlockingStub = ImageUploadGrpc.newBlockingStub(channel);
        AsyncStub = ImageUploadGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        Channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void startStream(final String filepath) {
        logger.info("tid: " +  Thread.currentThread().getId() + ", Will try to getBlob");
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

    public static void main(String[] args) throws Exception {
        UploadFileClient2 client2 = new UploadFileClient2("localhost", PORT);
        try {
            Long time = System.currentTimeMillis();
            client2.startStream("/Users/akhil-pt6225/Downloads/test2.jpg");
            Long time1 = System.currentTimeMillis()-time;
            logger.info("Time taken for streaming is " +time1);
            logger.info("Done with startStream");
        } finally {
            client2.shutdown();
        }
    }
}