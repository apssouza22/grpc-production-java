package com.apssouza.grpc;

import com.google.protobuf.Any;
import com.google.rpc.BadRequest;
import com.google.rpc.Code;
import com.google.rpc.Status;

import java.util.ArrayList;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;
import static io.grpc.protobuf.StatusProto.toStatusRuntimeException;

/**
 * Default server health check service
 */
public final class HealthCheckService extends HealthGrpc.HealthImplBase {

    public HealthCheckService() {
    }

    public void check(HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
        Status status = getErrorStatus();
        responseObserver.onError(toStatusRuntimeException(status));

        //        HealthCheckResponse.Builder builder = HealthCheckResponse.newBuilder();
        //        builder.setStatus(HealthCheckResponse.ServingStatus.SERVING);
        //        responseObserver.onNext(builder.build());
        //        responseObserver.onCompleted();
    }

    private Status getErrorStatus() {
        ArrayList<BadRequest.FieldViolation> violations = new ArrayList<>();
        BadRequest.FieldViolation fieldViolation = BadRequest.FieldViolation.newBuilder()
                .setDescription("test")
                .setField("test field").build();
        BadRequest.FieldViolation fieldViolation2 = BadRequest.FieldViolation.newBuilder()
                .setDescription("test")
                .setField("test field").build();
        violations.add(fieldViolation);
        violations.add(fieldViolation2);
        BadRequest badRequest = BadRequest.newBuilder()
                .addAllFieldViolations(violations)
                .build();


        Status status = Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT.getNumber())
                .setMessage("message error")
                .addDetails(Any.pack(badRequest))
                .build();
        return status;
    }
}

