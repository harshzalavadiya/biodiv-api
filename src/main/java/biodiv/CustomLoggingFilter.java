package biodiv;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class CustomLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
	
	private static final Logger log = LoggerFactory.getLogger(CustomLoggingFilter.class);

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Note down the start request time...we will use to calculate the total
		// execution time
		MDC.put("start-time", String.valueOf(System.currentTimeMillis()));

		log.info("Entering in Resource : "+ requestContext.getUriInfo().getPath()+" on method "+ resourceInfo.getResourceMethod().getName()+" in class : "+ resourceInfo.getResourceClass().getCanonicalName());
		logQueryParameters(requestContext);
		logMethodAnnotations();
		logRequestHeader(requestContext);
	}

	private void logQueryParameters(ContainerRequestContext requestContext) {
		Iterator iterator = requestContext.getUriInfo().getPathParameters().keySet().iterator();
		while (iterator.hasNext()) {
			String name = (String) iterator.next();
			List<String> obj = requestContext.getUriInfo().getPathParameters().get(name);
			String value = null;
			if (null != obj && obj.size() > 0) {
				value = obj.get(0);
			}
			log.debug("Query Parameter Name: {}, Value :{}", name, value);
		}
	}

	private void logMethodAnnotations() {
		Annotation[] annotations = resourceInfo.getResourceMethod().getDeclaredAnnotations();
		if (annotations != null && annotations.length > 0) {
			log.trace("----Start Annotations of resource ----");
			for (Annotation annotation : annotations) {
				log.trace(annotation.toString());
			}
			log.trace("----End Annotations of resource----");
		}
	}

	private void logRequestHeader(ContainerRequestContext requestContext) {
		Iterator<String> iterator;
		log.trace("----Start Header Section of request ----");
		log.trace("Method Type : {}", requestContext.getMethod());
		iterator = requestContext.getHeaders().keySet().iterator();
		while (iterator.hasNext()) {
			String headerName = iterator.next();
			String headerValue = requestContext.getHeaderString(headerName);
			log.trace("Header Name: {}, Header Value :{} ", headerName, headerValue);
		}
		log.trace("----End Header Section of request ----");
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		String stTime = MDC.get("start-time");
		if (null == stTime || stTime.length() == 0) {
			return;
		}
		long startTime = Long.parseLong(stTime);
		long executionTime = System.currentTimeMillis() - startTime;
		log.info("Total request execution time : {} milliseconds", executionTime);
		// clear the context on exit
		MDC.clear();
		/*
		 * StringBuilder sb = new StringBuilder();
		 * sb.append("Header: ").append(responseContext.getHeaders());
		 * sb.append(" - Entity: ").append(responseContext.getEntity());
		 * System.out.println("HTTP RESPONSE : " + sb.toString());
		 */
	}
}
