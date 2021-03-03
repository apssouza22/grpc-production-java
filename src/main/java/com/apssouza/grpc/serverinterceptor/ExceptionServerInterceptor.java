package com.apssouza.grpc.serverinterceptor;


import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * Interceptor to catch exception occurred in the request
 */
class ExceptionServerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        Listener<ReqT> delegate = next.startCall(call, headers);
        return new ExceptionListener<ReqT, RespT>(delegate, call);
    }

}
