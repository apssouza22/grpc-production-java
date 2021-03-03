package com.apssouza.grpc.server;

import com.apssouza.grpc.serverinterceptor.Factory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerInterceptor;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

/**
 * gRPC server builder
 */
public class GrpcServerBuilder {

    private NettyServerBuilder serverBuilder;

    /**
     * The port that the server will listener
     *
     * @param port
     * @return
     */
    public static GrpcServerBuilder port(int port) {
        GrpcServerBuilder grpcServerBuilder = new GrpcServerBuilder();
        grpcServerBuilder.serverBuilder = NettyServerBuilder.forPort(port);
        return grpcServerBuilder;
    }

    /**
     * The Executor that you provide is what actually executes the callbacks of the rpc.
     * <p>
     * This frees up the EventLoop to continue processing data on the connection. When a new message arrives from the
     * network, it is read on the event loop, and then propagated up the stack to the executor. The executor takes the
     * messages and passes them to your ServerCall.Listener which will actually do the processing of the data.
     * <p>
     * By default, gRPC uses a cached thread pool so that it is very easy to get started. However it is strongly
     * recommended you provide your own executor. The reason is that the default thread pool behaves badly under load,
     * creating new threads when the rest are busy.
     *
     * @param numThreads
     * @return
     */
    public GrpcServerBuilder fixedThreadPool(int numThreads) {
        serverBuilder.executor(Executors.newFixedThreadPool(numThreads));
        return this;
    }

    /**
     * MaxConnectionAge is a duration for the maximum amount of time a connection may exist before it will be closed by
     * sending a GoAway. A random jitter of +/-10% will be added to MaxConnectionAge to spread out connection storms.
     *
     * @return
     */
    public GrpcServerBuilder maxConnectionAge(long timeout, TimeUnit unit) {
        serverBuilder.maxConnectionAge(timeout, unit);
        return this;
    }

    /**
     * MaxConnectionIdle is a duration for the amount of time after which an idle connection would be closed by sending
     * a GoAway. Idleness duration is defined since the most recent time the number of outstanding RPCs became zero or
     * the connection establishment.
     *
     * @return
     */
    public GrpcServerBuilder maxConnectionIdle(long timeout, TimeUnit unit) {
        serverBuilder.maxConnectionIdle(timeout, unit);
        return this;
    }

    /**
     * After a duration of this time if the server doesn't see any activity it pings the client to see if the transport
     * is still alive. If set below 1s, a minimum value of 1s will be used instead.
     *
     * @return
     */
    public GrpcServerBuilder permitKeepAliveTime(long timeout, TimeUnit unit) {
        serverBuilder.permitKeepAliveTime(timeout, unit);
        return this;
    }

    /**
     * After having pinged for keepalive check, the server waits for a duration of Timeout and if no activity is seen
     * even after that the connection is closed.
     *
     * @return
     */
    public GrpcServerBuilder keepAliveTimeout(long timeout, TimeUnit unit) {
        serverBuilder.keepAliveTimeout(timeout, unit);
        return this;
    }

    /**
     * Set a list of services
     *
     * @param services
     * @return
     */
    public GrpcServerBuilder services(final List<BindableService> services) {
        services.forEach(serverBuilder::addService);
        return this;
    }

    /**
     * Set a list of interceptors
     *
     * @param interceptors
     * @return
     */
    public GrpcServerBuilder interceptors(final List<ServerInterceptor> interceptors) {
        ServerInterceptor aggregate = Factory.aggregateServerInterceptor(interceptors);
        serverBuilder.intercept(aggregate);
        return this;
    }

    /**
     * Build the gRPC server
     *
     * @return The gRPC server wrapper
     */
    public GrpcServer build() {
        Server server = serverBuilder.build();
        return new GrpcServer(server);
    }
}
