import com.akhil.test.ImageUploadGrpc;
import com.akhil.test.PutRequest;
import com.akhil.test.PutResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
public class UploadFileServer {
    private static final Logger logger = Logger.getLogger(UploadFileServer.class.getName());
    private static final int PORT = 50054;

    private Server Server;
    private void start() throws IOException {
        Server = ServerBuilder.forPort(PORT)
                .addService(new UploadFileServer.ImageUploadImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC Server since JVM is shutting down");
                UploadFileServer.this.stop();
                System.err.println("*** Server shut down");
            }
        });
    }

    private void stop() {
        if (Server != null) {
            Server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (Server != null) {
            Server.awaitTermination();
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        final UploadFileServer server = new UploadFileServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class ImageUploadImpl extends ImageUploadGrpc.ImageUploadImplBase {
        private int Status = 200;
        private String Message = "";
        private BufferedOutputStream bufferedOutputStream = null;

        @Override
        public StreamObserver<PutRequest> getBlob(final StreamObserver<PutResponse> responseObserver) {
            return new StreamObserver<PutRequest>() {
                int Count = 0;

                @Override
                public void onNext(PutRequest request) {
                    logger.info("onNext count: " + Count);
                    Count++;

                    byte[] data = request.getData().toByteArray();
                    long offset = request.getOffset();
                    String name = request.getName();
                    String temp = "";

                    for(int i = name.length()-1 ; i >= 0 ;i--){
                        if(name.charAt(i) == ('/')){
                            break;
                        }
                        temp = name.charAt(i)+ temp;
                    }
                     name = "/Users/akhil-pt6225/Downloads/ImageUploadWithMultipleClients/src/main/"+temp;
                    try {
                        if (bufferedOutputStream == null) {
                            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(name));
                        }
                        bufferedOutputStream.write(data);
                        bufferedOutputStream.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {
                    try {
                        responseObserver.onNext(PutResponse.newBuilder().setStatus(Status).setMessage(Message).build());
                        responseObserver.onCompleted();
                        if (bufferedOutputStream != null) {
                            try {
                                bufferedOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                bufferedOutputStream = null;
                            }
                        }
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
        }
    }
}