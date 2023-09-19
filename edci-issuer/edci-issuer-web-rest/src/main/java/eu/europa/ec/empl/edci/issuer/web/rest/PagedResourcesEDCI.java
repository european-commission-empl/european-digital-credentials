//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package eu.europa.ec.empl.edci.issuer.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;

import java.util.Arrays;
import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResourcesEDCI<T> extends PagedResources<T> {

    protected PagedResourcesEDCI() {
        super();
    }

    public PagedResourcesEDCI(Collection<T> content, PagedResources.PageMetadata metadata, Link... links) {
        super(content, metadata, (Iterable) Arrays.asList(links));
    }

    public PagedResourcesEDCI(Collection<T> content, PagedResources.PageMetadata metadata, Iterable<Link> links) {
        super(content, metadata, links);
    }
}
