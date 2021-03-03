package com.apssouza.grpc.serverinterceptor;

import static com.apssouza.grpc.GrpcHelper.ERROR;
import static com.apssouza.grpc.GrpcHelper.EVENT_ID;
import static com.apssouza.grpc.GrpcHelper.HEALTH_CHECK_METHOD_NAME;
import static com.apssouza.grpc.GrpcHelper.STATUS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Duration;
import java.time.Instant;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;

/**
 * Wrapper gRPC call to audit the response
 *
 * @param <ReqT>
 * @param <RespT>
 */
class AuditResponseCall<ReqT, RespT> extends ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditResponseCall.class);

    private final Instant start;
    private final Metadata headers;
    private final ServerCall call;
    private RespT message;

    protected AuditResponseCall(final ServerCall call, final Instant start, final Metadata headers) {
        super(call);
        this.call = call;
        this.start = start;
        this.headers = headers;
    }

    @Override
    public void sendMessage(RespT message) {
        this.message = message;
        delegate().sendMessage(message);
    }


    @Override
    public void close(Status status, Metadata metadata) {
        String methodName = call.getMethodDescriptor().getFullMethodName();
        boolean isHealthCheck = methodName.equals(HEALTH_CHECK_METHOD_NAME);
        if (!isHealthCheck) {
            logRequest(status);
        }
        super.close(status, metadata);
    }

    private void logRequest(final Status status) {
        MDC.put("duration_ms", String.format("%s", Duration.between(start, Instant.now()).toMillis()));
        MDC.put(EVENT_ID, "RESPONSE_AUDIT");
        MDC.put(STATUS, status.getCode().name());
        MDC.put(ERROR, status.getDescription());
        if (LOG.isDebugEnabled()) {
            MDC.put("additional-info", message.toString());
        }

        if (status.getCode() == Status.Code.OK) {
            LOG.info("Outgoing response");
            return;
        }

        if (isInvalidRequest(status.getCode())) {
            LOG.warn("gRPC call failed");
            return;
        }
        LOG.error("gRPC call errored");
    }

    private boolean isInvalidRequest(final Status.Code status) {
        if (status == Status.Code.CANCELLED) {
            return true;
        }
        if (status == Status.Code.INVALID_ARGUMENT) {
            return true;
        }
        if (status == Status.Code.NOT_FOUND) {
            return true;
        }
        if (status == Status.Code.ALREADY_EXISTS) {
            return true;
        }
        if (status == Status.Code.PERMISSION_DENIED) {
            return true;
        }
        if (status == Status.Code.FAILED_PRECONDITION) {
            return true;
        }
        if (status == Status.Code.ABORTED) {
            return true;
        }
        if (status == Status.Code.OUT_OF_RANGE) {
            return true;
        }
        if (status == Status.Code.UNIMPLEMENTED) {
            return true;
        }
        if (status == Status.Code.UNAUTHENTICATED) {
            return true;
        }
        return false;
    }
}
