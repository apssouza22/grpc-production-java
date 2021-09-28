package com.apssouza.grpc.serverinterceptor;


import com.google.protobuf.Any;
import com.google.rpc.BadRequest;
import com.google.rpc.Code;
import com.google.rpc.Status;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.internal.NoopServerCall;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import static io.grpc.protobuf.StatusProto.toStatusRuntimeException;

public class AuditServerInterceptorTest {

    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    /**
     * A generated unique in-process server name.
     */
    private String serverName;

    @Test
    public void testConstructor() {
        NoopServerCall<Object, Object> noopServerCall = new NoopServerCall<>();
        Metadata metadata = new Metadata();
        AuditResponseCall<Object, Object> auditResponseCall = new AuditResponseCall<>(noopServerCall, null, metadata);
        assertTrue(auditResponseCall.isReady());
        assertFalse(auditResponseCall.isCancelled());
    }

    @Before
    public void setUp() {
        // Generate a unique in-process server name.
        this.serverName = InProcessServerBuilder.generateName();
    }


    @Test
    public void validateException() throws IOException {
        AggregateServerInterceptor interceptors = new AggregateServerInterceptor(new AuditServerInterceptor());
        Server server = InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(ServerInterceptors.intercept(new HealthTestService(), interceptors))
                .build()
                .start();

        ManagedChannel client = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();

        // Register for automatic graceful shutdown.
        grpcCleanup.register(server);
        grpcCleanup.register(client);


        HealthGrpc.HealthBlockingStub healthBlockingStub = HealthGrpc.newBlockingStub(client);
        final HealthCheckRequest healthCheckRequest = HealthCheckRequest.newBuilder().build();

        Assertions.assertThatThrownBy(() -> healthBlockingStub.check(healthCheckRequest))
                .isInstanceOf(io.grpc.StatusRuntimeException.class)
                .hasMessageContaining("INVALID_ARGUMENT: message error");
    }


    /*
     * Dummy service to throw exception to test GrpcExceptionInterceptor.
     */
    class HealthTestService extends HealthGrpc.HealthImplBase {
        public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
            BadRequest badRequest = BadRequest.newBuilder()
                    .build();
            Status status = Status.newBuilder()
                    .setCode(Code.INVALID_ARGUMENT.getNumber())
                    .setMessage("message error")
                    .addDetails(Any.pack(badRequest))
                    .build();
            responseObserver.onError(toStatusRuntimeException(status));
        }
    }
}

