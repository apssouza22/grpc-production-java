package com.apssouza.grpc.serverinterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;


/**
 * {@code CancelledRequestInterceptor} logs the request cancelled with the total time taken
 */
public class CancelledRequestInterceptor implements ServerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(CancelledRequestInterceptor.class);

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        ServerCall.Listener<ReqT> listener;
        Instant start = Instant.now();
        listener = next.startCall(call, headers);
        if (call.isCancelled()) {
            LOG.warn(String.format(
                    "Request cancelled after %s \n method %s \n address %s ",
                    Duration.between(start, Instant.now()).toMillis(),
                    call.getMethodDescriptor().getFullMethodName(),
                    call.getAttributes().get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR)
            ));
        }

        return listener;
    }


}

