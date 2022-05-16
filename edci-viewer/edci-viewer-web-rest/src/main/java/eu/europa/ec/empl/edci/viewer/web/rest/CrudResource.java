package eu.europa.ec.empl.edci.viewer.web.rest;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface CrudResource {

    public class Oids {

        public List<Long> oid = new ArrayList<>();

        public List<Long> getOid() {
            return oid;
        }

        public Long getSingleOid() {
            return oid != null && !oid.isEmpty() ? oid.get(0) : null;
        }

        public void setOid(List<Long> oid) {
            this.oid = oid;
        }

    }


    default <T> ResponseEntity<T> generateResponse(T entity, HttpStatus status) {
        return new ResponseEntity<T>(entity, status);
    }

    default <T> ResponseEntity<Resource<T>> generateResponse(T entity, HttpStatus status, Link... hateoas) {
        return new ResponseEntity<Resource<T>>(new Resource<T>(entity, hateoas), status);
    }

    default ResponseEntity generateOkResponse(Object entity, Link... hateoas) {
        return generateResponse(entity, HttpStatus.OK, hateoas);
    }

    default ResponseEntity generateCreatedResponse(Object entity, Link... hateoas) {
        return generateResponse(entity, HttpStatus.CREATED, hateoas);
    }


}
