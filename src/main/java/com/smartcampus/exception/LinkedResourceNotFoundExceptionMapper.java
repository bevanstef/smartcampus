package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 422);
        errorResponse.put("error", "Unprocessable Entity");
        errorResponse.put("message", exception.getMessage());

        // 422 is not a default constant in standard JAX-RS Response.Status, so we pass
        // the integer directly
        return Response.status(422)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}