package com.apssouza.grpc;

import com.apssouza.grpc.clientinterceptor.Factory;

import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .intercept(Factory.allDefaultInterceptors())
                .build();

        HealthCheckRequest request = HealthCheckRequest.newBuilder().build();
        HealthGrpc.HealthBlockingStub blockingStub = HealthGrpc.newBlockingStub(channel);
        HealthCheckResponse check = blockingStub.check(request);

        System.out.println(String.format("result %s", check.getStatus()));
    }
}
