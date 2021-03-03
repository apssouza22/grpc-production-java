package com.apssouza.grpc.serverinterceptor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * gRPC request/response audit interceptor
 */
class AuditServerInterceptor implements ServerInterceptor {


    private static Logger LOG = LoggerFactory.getLogger(AuditServerInterceptor.class);

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {

        // MDC implementations uses ThreadLocals to store the contextual information
        // Since gRPC uses thread pool, the same thread is used by many requests
        // Clear makes sure different requests don't share context information
        MDC.clear();

        Instant start = Instant.now();
        AuditResponseCall wrappedCall = new AuditResponseCall(call, start, headers);

        Listener<ReqT> listener = next.startCall(wrappedCall, headers);

        return new AuditRequestListener(listener, headers, call);
    }
}
