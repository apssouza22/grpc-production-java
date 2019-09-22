package com.apssouza.server.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.util.List;
import java.util.concurrent.Executors;

public class GrpcServerBuilder {

    private ServerBuilder<?> serverBuilder;

    public static GrpcServerBuilder port(int port) {
        GrpcServerBuilder grpcServerBuilder = new GrpcServerBuilder();
        grpcServerBuilder.serverBuilder = ServerBuilder.forPort(port);
        return grpcServerBuilder;
    }

    public GrpcServerBuilder fixedThreadPool(int numThreads) {
        serverBuilder.executor(Executors.newFixedThreadPool(numThreads));
        return this;
    }

    public GrpcServerBuilder services(final List<BindableService> services) {
        services.forEach(serverBuilder::addService);
        return this;
    }

    public GrpcServer build() {
        Server server = serverBuilder.build();
        return new GrpcServer(server);
    }
}
