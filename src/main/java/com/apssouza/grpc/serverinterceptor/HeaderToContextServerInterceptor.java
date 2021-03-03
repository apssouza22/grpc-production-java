package com.apssouza.grpc.serverinterceptor;


import com.apssouza.grpc.GrpcHelper;

import java.util.HashMap;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * Set gRPC headers to context. The data will then be available in the RPC implementation and the client interceptor
 */
class HeaderToContextServerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        HashMap<String, String> headers = new HashMap<>();
        for (String key : metadata.keys()) {
            String value = metadata.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
            headers.put(key, value);
        }
        Context context = Context.current().withValue(GrpcHelper.CTX_HEADERS, headers);
        return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }
}
