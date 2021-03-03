package com.apssouza.grpc;

import com.google.common.base.Verify;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import javax.annotation.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;

public class ClientStarter {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        HealthCheckRequest request = HealthCheckRequest.newBuilder().build();
        HealthGrpc.HealthBlockingStub blockingStub = HealthGrpc.newBlockingStub(channel);
        HealthCheckResponse check = blockingStub.check(request);
        System.out.println(String.format("result %s", check.getStatus()));

        HealthGrpc.HealthFutureStub stub = HealthGrpc.newFutureStub(channel);
        ListenableFuture<HealthCheckResponse> response = stub.check(request);

        final CountDownLatch latch = new CountDownLatch(1);

        Futures.addCallback(
                response,
                new FutureCallback<HealthCheckResponse>() {
                    @Override
                    public void onSuccess(@Nullable HealthCheckResponse result) {
                        System.out.println(result.getStatus());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Status status = Status.fromThrowable(t);
                        Verify.verify(status.getCode() == Status.Code.INTERNAL);
                        Verify.verify(status.getDescription().contains("Crybaby"));
                        // Cause is not transmitted over the wire..
                        latch.countDown();
                    }
                },
                directExecutor()
        );

        if (!Uninterruptibles.awaitUninterruptibly(latch, 1, TimeUnit.SECONDS)) {
            throw new RuntimeException("timeout!");
        }
    }
}
