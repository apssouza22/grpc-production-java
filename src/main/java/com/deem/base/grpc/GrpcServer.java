package com.deem.base.grpc;


import io.grpc.Server;

import java.io.IOException;
import java.util.logging.Logger;

public class GrpcServer {
    private static Logger LOG = Logger.getLogger(GrpcServer.class.getName());
    private Server server;

    public GrpcServer(Server server) {
        this.server = server;
    }

    public void start() throws IOException, InterruptedException {
        registerShutDownListener();
        this.server.start();
        LOG.info("GRPC server stated");
        server.awaitTermination();
    }

    private void registerShutDownListener() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    private void cleanup() {
        this.server.shutdown();
        try {
            this.server.awaitTermination();
        } catch (InterruptedException e) {
            LOG.warning("GRPC server shutdown error.");
        }
    }

}
