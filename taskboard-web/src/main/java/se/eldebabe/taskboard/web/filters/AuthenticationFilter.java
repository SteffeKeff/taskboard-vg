package se.eldebabe.taskboard.web.filters;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import se.eldebabe.taskboard.web.services.LoginWebService;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter{
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		SecurityContext originalContext = requestContext.getSecurityContext();
		
		String token = requestContext.getHeaders().getFirst("Token");
			
		if(LoginWebService.tokens.contains(token)){
			
			Set<String> roles = new HashSet<>();
			
			roles.add("USER");
			
			Authorizer authorizer = new Authorizer(roles, "user", originalContext.isSecure());
			requestContext.setSecurityContext(authorizer);
		}
		
	}
	
	public static class Authorizer implements SecurityContext {

        Set<String> roles;
        String username;
        boolean isSecure;
        
        public Authorizer(Set<String> roles, final String username, 
                                             boolean isSecure) {
            this.roles = roles;
            this.username = username;
            this.isSecure = isSecure;
        }

        @Override
        public boolean isUserInRole(String role) {
            return roles.contains(role);
        }

        @Override
        public boolean isSecure() {
            return isSecure;
        }

        @Override
        public String getAuthenticationScheme() {
            return "Your Scheme";
        }

		@Override
		public Principal getUserPrincipal() {
			return new User(username);
		} 
    } 
	
	public static class User implements Principal {
        String name;

        public User(String name) {
            this.name = name;
        }

        @Override
        public String getName() { return name; }   
	}

}
