package com.apssouza.grpc;

import com.apssouza.grpc.server.GrpcServer;
import com.apssouza.grpc.server.GrpcServerBuilder;
import com.apssouza.grpc.serverinterceptor.Factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.BindableService;
import io.grpc.ServerInterceptor;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        List<BindableService> services = new ArrayList<>();
        List<ServerInterceptor> interceptors = Factory.allDefaultInterceptors();
        GrpcServerBuilder builder = GrpcServerBuilder.port(50052);
        GrpcServer grpcServer = builder
                .withServices(services)
                .withInterceptors(interceptors)
                .withFixedThreadPool(4)
                .withMaxConnectionAge(5, TimeUnit.MINUTES)
                .withReflectionEnabled(true)
                .withHealthCheck()
                .build();
        grpcServer.start();
    }
}
