/*
 *  Copyright (c) 2019, Salesforce.com, Inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.apssouza.grpc.clientinterceptor;


import org.junit.Rule;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import io.grpc.MethodDescriptor;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;

public class AuditClientInterceptorTest {
    @Rule
    public final GrpcServerRule serverRule = new GrpcServerRule().directExecutor();

    HealthGrpc.HealthImplBase svc = new HealthGrpc.HealthImplBase() {
        @Override
        public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
            responseObserver.onNext(HealthCheckResponse.newBuilder().build());
            responseObserver.onCompleted();
        }
    };

    @Test
    public void clientStopwatchWorks() {
        AtomicReference<MethodDescriptor> startDesc = new AtomicReference<>();
        AtomicReference<MethodDescriptor> stopDesc = new AtomicReference<>();
        AtomicReference<Duration> stopDur = new AtomicReference<>();

        //Setup
        serverRule.getServiceRegistry().addService(svc);
        HealthGrpc.HealthBlockingStub stub = HealthGrpc
                .newBlockingStub(serverRule.getChannel())
                .withInterceptors(new AuditClientInterceptor() {
                    @Override
                    protected void logStart(MethodDescriptor method) {
                        startDesc.set(method);
                    }

                    @Override
                    protected void logStop(MethodDescriptor method, Duration duration) {
                        stopDesc.set(method);
                        stopDur.set(duration);
                    }
                });

        stub.check(HealthCheckRequest.newBuilder().build());

        assertThat(startDesc.get().getFullMethodName()).contains("Check");
        assertThat(startDesc.get().getFullMethodName()).contains("Check");
        assertThat(stopDur.get()).isGreaterThan(Duration.ZERO);
    }
}
