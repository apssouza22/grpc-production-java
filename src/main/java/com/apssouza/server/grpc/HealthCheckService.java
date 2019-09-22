package com.apssouza.server.grpc;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;


public final class HealthCheckService extends HealthGrpc.HealthImplBase {

    public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
        HealthCheckResponse.Builder builder = HealthCheckResponse.newBuilder();
        builder.setStatus(HealthCheckResponse.ServingStatus.SERVING);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
