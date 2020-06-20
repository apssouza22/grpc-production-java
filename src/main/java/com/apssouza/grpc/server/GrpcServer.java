package com.apssouza.grpc.server;


import com.apssouza.grpc.utils.ShutdownHelper;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Logger;

import io.grpc.Server;

/**
 * gRPC server wrapper
 */
public class GrpcServer {
    private static Logger LOG = Logger.getLogger(GrpcServer.class.getName());
    private Server server;

    public GrpcServer(Server server) {
        this.server = server;
    }

    public void start() throws IOException, InterruptedException {
        registerShutDownListener();
        this.server.start();
        LOG.info("GRPC server stated on port=" + server.getPort());
        server.awaitTermination();
    }

    private void registerShutDownListener() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    private void cleanup() {
        LOG.warning("GRPC server shutting down");
        ShutdownHelper.shutdownWithJvm(this.server, Duration.ofMillis(5).toMillis());
    }

}
