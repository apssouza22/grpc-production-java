package com.apssouza.grpc.serverinterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;

/**
 * Listener for exceptions occurred in the request
 *
 * @param <ReqT>
 * @param <RespT>
 */
class ExceptionListener<ReqT, RespT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionServerInterceptor.class);
    private final ServerCall<ReqT, RespT> call;

    protected ExceptionListener(final ServerCall.Listener delegate, ServerCall<ReqT, RespT> call) {
        super(delegate);
        this.call = call;
    }

    @Override
    public void onHalfClose() {
        try {
            super.onHalfClose();
        } catch (Exception exception) {
            LOG.error("Error while processing the request {}", exception);
            Status error = Status.INTERNAL
                    .withCause(exception)
                    .withDescription("Error while processing the request");
            call.close(error, new Metadata());
        }
    }
}
