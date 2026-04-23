package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    // This method runs right BEFORE your API processes a request
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info(">>> INCOMING REQUEST: " + requestContext.getMethod() + " "
                + requestContext.getUriInfo().getAbsolutePath());
    }

    // This method runs right AFTER your API builds the response, before sending it
    // to the client
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        LOGGER.info("<<< OUTGOING RESPONSE: Status " + responseContext.getStatus());
    }
}