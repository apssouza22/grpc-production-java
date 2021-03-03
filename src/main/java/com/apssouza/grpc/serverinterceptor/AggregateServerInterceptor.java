
package com.apssouza.grpc.serverinterceptor;

import com.google.common.collect.Lists;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * AggregateClientInterceptor is used to bundle multiple interceptor implementations into a single interceptor. Each inner {@code ServerInterceptor} is applied in order when AggregateServerInterceptor
 * is called.
 */
class AggregateServerInterceptor implements ServerInterceptor {
    private final List<ServerInterceptor> interceptors;

    /**
     * Construct a AggregateServerInterceptor from one or more {@link ServerInterceptor}s. The inner {@code
     * ClientInterceptor}s will be called in order.
     *
     * @param interceptors a {@link ServerInterceptor} array
     */
    public AggregateServerInterceptor(ServerInterceptor... interceptors) {
        this(Arrays.asList(checkNotNull(interceptors, "interceptors")));
    }

    /**
     * Construct a AggregateServerInterceptor from one or more {@link ServerInterceptor}s. The inner {@code
     * ClientInterceptor}s will be called in order.
     *
     * @param interceptors a {@link ServerInterceptor} list
     */
    public AggregateServerInterceptor(List<ServerInterceptor> interceptors) {
        checkNotNull(interceptors, "interceptors");
        checkArgument(interceptors.size() > 0,
                "AggregateServerInterceptor requires at least one inner ServerInterceptor.");
        this.interceptors = interceptors;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next
    ) {
        // reverse the interceptors list so that the last interceptor to call is the most nested interceptor
        for (ServerInterceptor interceptor : Lists.reverse(interceptors)) {
            next = new InterceptorServerCallHandler<>(next, interceptor);
        }

        return next.startCall(call, headers);
    }

    /**
     * A {@link ServerCallHandler} implementation used to chain {@link ServerInterceptor} instances together.
     *
     * @param <ReqT>
     * @param <RespT>
     */
    private static final class InterceptorServerCallHandler<ReqT, RespT> implements ServerCallHandler<ReqT, RespT> {
        private final ServerCallHandler<ReqT, RespT> next;
        private final ServerInterceptor interceptor;

        private InterceptorServerCallHandler(ServerCallHandler<ReqT, RespT> next, ServerInterceptor interceptor) {
            this.next = next;
            this.interceptor = interceptor;
        }

        @Override
        public ServerCall.Listener<ReqT> startCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
            return interceptor.interceptCall(serverCall, metadata, next);
        }
    }
}
