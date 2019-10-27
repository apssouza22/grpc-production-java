package com.apssouza.grpc.server;


import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.grpc.Server;

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
        try {
            this.server.shutdown();
            this.server.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warning("GRPC server shutdown error.");
        }
    }

}
