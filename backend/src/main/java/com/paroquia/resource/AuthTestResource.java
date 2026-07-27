package com.paroquia.resource;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Set;

@Path("/api/auth-test")
public class AuthTestResource {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/publico")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String publico() {
        return "Endpoint público, sem autenticação.";
    }

    @GET
    @Path("/autenticado")
    @Produces(MediaType.TEXT_PLAIN)
    public String autenticado() {
        String username = securityIdentity.getPrincipal().getName();
        Set<String> roles = securityIdentity.getRoles();
        return "Autenticado como: " + username + " | roles: " + roles;
    }

    @GET
    @Path("/super-admin")
    @RolesAllowed("super-admin")
    @Produces(MediaType.TEXT_PLAIN)
    public String somenteSuperAdmin() {
        return "Acesso permitido: super-admin";
    }

    @GET
    @Path("/admin-pastoral")
    @RolesAllowed("admin-pastoral")
    @Produces(MediaType.TEXT_PLAIN)
    public String somenteAdminPastoral() {
        return "Acesso permitido: admin-pastoral";
    }

    @GET
    @Path("/padre")
    @RolesAllowed("padre")
    @Produces(MediaType.TEXT_PLAIN)
    public String somentePadre() {
        return "Acesso permitido: padre";
    }
}