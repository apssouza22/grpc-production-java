package com.apssouza.grpc.server;

import com.apssouza.grpc.serverinterceptor.Factory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerInterceptor;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.services.HealthStatusManager;

/**
 * gRPC server builder
 */
public class GrpcServerBuilder {

    private NettyServerBuilder serverBuilder;

    public static GrpcServerBuilder port(int port) {
        GrpcServerBuilder grpcServerBuilder = new GrpcServerBuilder();
        grpcServerBuilder.serverBuilder = NettyServerBuilder.forPort(port);
        return grpcServerBuilder;
    }


    /**
     * The Executor that you provide is what actually executes the callbacks of the rpc.
     * <p>
     * This frees up the EventLoop to continue processing data on the connection. When a new message arrives from the network, it is read on the event loop, and then propagated up the stack to the
     * executor. The executor takes the messages and passes them to your ServerCall.Listener which will actually do the processing of the data.
     * <p>
     * By default, gRPC uses a cached thread pool so that it is very easy to get started. However it is strongly recommended you provide your own executor. The reason is that the default thread pool
     * behaves badly under load, creating new threads when the rest are busy.
     *
     * @param numThreads
     * @return
     */
    public GrpcServerBuilder withFixedThreadPool(int numThreads) {
        serverBuilder.executor(Executors.newFixedThreadPool(numThreads));
        return this;
    }

    /**
     * MaxConnectionAge is a duration for the maximum amount of time a connection may exist before it will be closed by sending a GoAway. MaxConnectionAge is just to avoid long connection, to
     * facilitate load balancing
     *
     * @return
     */
    public GrpcServerBuilder withMaxConnectionAge(long timeout, TimeUnit unit) {
        serverBuilder.maxConnectionAge(timeout, unit);
        return this;
    }

    /**
     * Set a list of gRPC services
     *
     * @param services
     * @return
     */
    public GrpcServerBuilder withServices(final List<BindableService> services) {
        services.forEach(serverBuilder::addService);
        return this;
    }

    /**
     * Enable server reflection
     *
     * @return
     */
    public GrpcServerBuilder withReflectionEnabled(boolean enable) {
        if (enable) {
            serverBuilder.addService(ProtoReflectionService.newInstance());
        }
        return this;
    }

    /**
     * Create default health check service
     *
     * @return
     */
    public GrpcServerBuilder withHealthCheck() {
        serverBuilder.addService(new HealthStatusManager().getHealthService());
        return this;
    }

    /**
     * Set a list of interceptors
     *
     * @param interceptors
     * @return
     */
    public GrpcServerBuilder withInterceptors(final List<ServerInterceptor> interceptors) {
        serverBuilder.intercept(Factory.aggregateServerInterceptor(interceptors));
        return this;
    }

    /**
     * Build the GrpcServer
     *
     * @return
     */
    public GrpcServer build() {
        Server server = serverBuilder.build();
        return new GrpcServer(server);
    }
}