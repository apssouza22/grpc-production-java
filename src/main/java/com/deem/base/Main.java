package com.deem.base;

import com.deem.base.grpc.GrpcServer;
import com.deem.base.grpc.GrpcServerBuilder;
import com.deem.base.grpc.HealthCheckService;
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
