package com.apssouza.grpc.interceptors;


import java.time.Instant;
import java.util.logging.Logger;

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;


/**
 * A class that intercepts uncaught exceptions of all types and handles them by closing the {@link ServerCall}. This
 * class is a complement to gRPC's {@code TransmitStatusRuntimeExceptionInterceptor}.
 *
 * <p>Without this interceptor, gRPC will strip all details and close the {@link ServerCall} with
 * a generic {@link Status#UNKNOWN} code.
 */
public class UnexpectedExceptionInterceptor implements ServerInterceptor {

    private static Logger LOG = Logger.getLogger(UnexpectedExceptionInterceptor.class.getName());

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        Instant start = Instant.now();
        ServerCall.Listener<ReqT> delegate = next.startCall(call, headers);
        return new SimpleForwardingServerCallListener<ReqT>(delegate) {
            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (Throwable exception) {
                    LOG.severe(String.format(
                            "Error while processing the request \n method %s \n address %s ",
                            call.getMethodDescriptor().getFullMethodName(),
                            call.getAttributes().get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR)
                    ));
                    call.close(
                            Status.INTERNAL
                                    .withCause(exception)
                                    .withDescription("Error while processing the request"),
                            new Metadata()
                    );
                }
            }
        };
    }
}


