package com.apssouza.server;

import com.apssouza.server.grpc.GrpcServer;
import com.apssouza.server.grpc.GrpcServerBuilder;
import com.apssouza.server.grpc.HealthCheckService;
import io.grpc.BindableService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        List<BindableService> services = new ArrayList<>();
        services.add(new HealthCheckService());
        GrpcServerBuilder builder = GrpcServerBuilder.port(5051);
        GrpcServer grpcServer = builder.services(services)
                .fixedThreadPool(4)
                .build();
        grpcServer.start();
    }
}
