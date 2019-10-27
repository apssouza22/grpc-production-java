package com.apssouza.grpc;

import com.apssouza.grpc.server.GrpcServer;
import com.apssouza.grpc.server.GrpcServerBuilder;
import com.apssouza.grpc.server.HealthCheckService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.BindableService;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        List<BindableService> services = new ArrayList<>();
        services.add(new HealthCheckService());
        GrpcServerBuilder builder = GrpcServerBuilder.port(50051);
        GrpcServer grpcServer = builder.services(services)
                .fixedThreadPool(4)
                .maxConnectionAge(5, TimeUnit.MINUTES)
                .build();
        grpcServer.start();
    }
}
