package com.apssouza.server.grpc;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;


class HealthCheckServiceTest {

    /**
     * This rule manages automatic graceful shutdown for the registered servers and channels at the
     * end of test.
     */
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    /**
     * A generated unique in-process server name.
     */
    private String serverName;

    @BeforeEach
    void setUp() {
        // Generate a unique in-process server name.
        this.serverName = InProcessServerBuilder.generateName();
    }

    @Test
    void check() throws IOException {
        Server server = InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(new HealthCheckService())
                .build()
                .start();

        ManagedChannel client = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();

        // Register for automatic graceful shutdown.
        grpcCleanup.register(server);
        grpcCleanup.register(client);

        HealthGrpc.HealthBlockingStub healthBlockingStub = HealthGrpc.newBlockingStub(client);

        HealthCheckRequest healthCheckRequest = HealthCheckRequest.newBuilder().build();
        HealthCheckResponse response = healthBlockingStub.check(healthCheckRequest);

        Assertions.assertEquals(response.getStatus(),HealthCheckResponse.ServingStatus.SERVING);
    }
}