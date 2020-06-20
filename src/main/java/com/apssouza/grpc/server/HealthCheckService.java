package com.apssouza.grpc.server;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;


/**
 * Default health check service implementation
 */
public final class HealthCheckService extends HealthGrpc.HealthImplBase {

    /**
     * Check if the service is responding
     *
     * @param request
     * @param responseObserver
     */
    public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
        HealthCheckResponse.Builder builder = HealthCheckResponse.newBuilder();
        builder.setStatus(HealthCheckResponse.ServingStatus.SERVING);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
