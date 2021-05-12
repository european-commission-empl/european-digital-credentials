package eu.europa.ec.empl.edci.wallet.web.servlet;

import eu.europa.ec.empl.edci.wallet.service.ShareLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VerifiablePresentationDownloadServlet {

    @Autowired
    private ShareLinkService shareLinkService;

    @GetMapping("/{shareHash}/pdf")
    public ResponseEntity<ByteArrayResource> downloadShareLinkPresentationPDF(@PathVariable("shareHash") String shareHash){
        return shareLinkService.downloadShareLinkPresentationPDF(shareHash);
    }


    @GetMapping("/{shareHash}/xml")
    public ResponseEntity<byte[]> downloadShareLinkPresentationXML(@PathVariable("shareHash") String shareHash){
        return shareLinkService.downloadShareLinkPresentationXML(shareHash);
    }

    @GetMapping("/{shareHash}")
    public ResponseEntity<byte[]> downloadDefaultShareLinkPresentation(@PathVariable("shareHash") String shareHash){
        return shareLinkService.downloadShareLinkPresentationXML(shareHash);
    }
}
