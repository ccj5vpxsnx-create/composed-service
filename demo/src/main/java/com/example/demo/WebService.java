package com.example.demo;

import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.jackson.JacksonFeature;

@Path("/service")
public class WebService {

    private static final String SERVICE1_URI = "http://localhost:8082/service";
    private static final String SERVICE2_URI = "http://localhost:3001/service";

    private final Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();

    @PreDestroy
    void fermer() {
        client.close();
    }
    @POST
    @Path("/inscriptions/{cin}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public NouveauNe inscrire(@PathParam("cin") String cin, NouveauNe nne) {
        if (cin == null || cin.isBlank()) {
            throw new BadRequestException("Le CIN est obligatoire");
        }
        if (nne == null) {
            throw new BadRequestException("Les données du nouveau-né sont obligatoires");
        }
        return composer(cin, nne);
    }
    // ---------- orchestrated service ----------
    private NouveauNe composer(String cin, NouveauNe nne) {

        // 1---- Appel du service citoyens
        Citoyen citoyen;

        try (Response r1 = client.target(SERVICE1_URI)
            .path("citoyens").path(cin)
            .request(MediaType.APPLICATION_JSON)
            .get()) {

            if (r1.getStatus() == 404) {
                throw new NotFoundException("Aucun citoyen trouvé avec le CIN " + cin);
            }
            if (r1.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                throw new WebApplicationException(
                    "Erreur du service Citoyens (HTTP " + r1.getStatus() + ")",
                    Response.Status.BAD_GATEWAY);
            }
            citoyen = r1.readEntity(Citoyen.class);

        } catch (ProcessingException e) {
            throw new WebApplicationException(
                "Service Citoyens indisponible", Response.Status.SERVICE_UNAVAILABLE);
        }

        // 2----- Compléter le NouveauNe avec les données du père
        nne.setCinPere(citoyen.getCin());
        nne.setNomPere(citoyen.getNom());
        nne.setPrenomPere(citoyen.getPrenomPere());

        // 3----- Appel du service nouveauNes
        try (Response r2 = client.target(SERVICE2_URI)
            .path("nouveaux_nes")
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(nne, MediaType.APPLICATION_JSON))) {

            if (r2.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                throw new WebApplicationException(
                    "Échec de l'inscription du nouveau-né (HTTP " + r2.getStatus() + ")",
                    Response.Status.BAD_GATEWAY);
            }
            return r2.readEntity(NouveauNe.class);

        } catch (ProcessingException e) {
            throw new WebApplicationException(
                "Service Nouveau-nés indisponible", Response.Status.SERVICE_UNAVAILABLE);
        }
    }
}
