package com.apssouza.grpc.server;


import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

import io.grpc.Server;

/**
 * Grpc wrapper for the Grpc NettyServer implementation This wrapper will try to gather all best practices and information when working with gRPC
 */
public class GrpcServer {

    private static Logger LOG = LoggerFactory.getLogger(GrpcServer.class);

    private Server server;

    public GrpcServer(Server server) {
        checkNotNull(server);
        this.server = server;
    }

    /**
     * Start the gRPC NettyServer
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void start() throws IOException, InterruptedException {
        registerShutDownListener();
        this.server.start();
        LOG.info("gRPC server started on port {}", server.getPort());
        server.awaitTermination();
    }

    private void registerShutDownListener() {
        ShutdownHelper.shutdownWithJvm(this.server, Duration.ofSeconds(5).toMillis());
    }

}
