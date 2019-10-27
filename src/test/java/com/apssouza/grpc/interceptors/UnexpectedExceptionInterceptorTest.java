package com.apssouza.grpc.interceptors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.grpc.ServerInterceptors;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;

public class UnexpectedExceptionInterceptorTest {
    @Rule
    public final GrpcServerRule serverRule = new GrpcServerRule();
    private HealthGrpc.HealthImplBase svc;
    private UnexpectedExceptionInterceptor interceptor;

    @Before
    public void setUP() {
        this.svc = new HealthGrpc.HealthImplBase() {
            @Override
            public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
                responseObserver.onNext(HealthCheckResponse.newBuilder().build());
                responseObserver.onCompleted();
            }
        };
        this.interceptor = new UnexpectedExceptionInterceptor();
    }

    @Test
    public void noExceptionDoesNotInterfere() {
        serverRule.getServiceRegistry().addService(ServerInterceptors.intercept(svc, interceptor));
        HealthGrpc.HealthBlockingStub stub = HealthGrpc.newBlockingStub(serverRule.getChannel());

        stub.check(HealthCheckRequest.newBuilder().build());
    }


    @Test
    public void alleMatches() {
        this.svc = new HealthGrpc.HealthImplBase() {
            @Override
            public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
                throw new ArithmeticException("Divide by zero");
            }
        };
        serverRule.getServiceRegistry().addService(ServerInterceptors.intercept(svc, interceptor));
        HealthGrpc.HealthBlockingStub stub = HealthGrpc.newBlockingStub(serverRule.getChannel());

        assertThatThrownBy(() -> stub.check(HealthCheckRequest.newBuilder().build()))
                .isInstanceOf(StatusRuntimeException.class)
                .matches(sre -> ((StatusRuntimeException) sre)
                        .getStatus().getCode()
                        .equals(Status.INTERNAL.getCode()), "is Status.INTERNAL")
                .hasMessageContaining("INTERNAL: Error while processing the request");
    }

}
