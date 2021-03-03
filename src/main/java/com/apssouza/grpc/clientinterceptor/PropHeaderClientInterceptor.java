package com.apssouza.grpc.clientinterceptor;


import com.apssouza.grpc.GrpcHelper;

import java.util.Map;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

/**
 * Propagate headers from context to the metadata headers
 */
class PropHeaderClientInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Map<String, String> headersMap = GrpcHelper.CTX_HEADERS.get();
                headersMap.forEach((key, value) -> {
                    headers.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value);
                });
                super.start(responseListener, headers);
            }
        };
    }
}
