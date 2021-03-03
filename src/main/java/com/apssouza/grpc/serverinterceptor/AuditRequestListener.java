package com.apssouza.grpc.serverinterceptor;

import com.apssouza.grpc.GrpcHelper;
import static com.apssouza.grpc.GrpcHelper.EVENT_ID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;

/**
 * gRPC listener to audit requests
 *
 * @param <ReqT>
 * @param <RespT>
 */
class AuditRequestListener<ReqT, RespT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<RespT> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditRequestListener.class);

    private final Metadata headers;
    private final ServerCall<ReqT, RespT> call;

    protected AuditRequestListener(final ServerCall.Listener delegate, Metadata headers, final ServerCall<ReqT, RespT> call) {
        super(delegate);
        this.headers = headers;
        this.call = call;
    }

    @Override
    public void onMessage(RespT message) {
        String methodName = call.getMethodDescriptor().getFullMethodName();
        boolean isHealthCheck = methodName.equals(GrpcHelper.HEALTH_CHECK_METHOD_NAME);
        if (!isHealthCheck) {
            MDC.put(EVENT_ID, "REQUEST_AUDIT");
            MDC.put("additional-info", message.toString());
            LOG.info("Incoming request");
            MDC.remove("additional-info");
            MDC.remove(EVENT_ID);
        }
        delegate().onMessage(message);
    }
}
