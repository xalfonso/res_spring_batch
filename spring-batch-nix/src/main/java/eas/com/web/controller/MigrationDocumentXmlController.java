package eas.com.web.controller;

import eas.com.service.MigrationDocumentXmlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@RequestMapping("migrationDocumentXml")
@RestController
public class MigrationDocumentXmlController {

    @Resource
    private MigrationDocumentXmlService migrationDocumentXmlService;


    @GetMapping("run/{year}/{truncateDestinationTable}")
    public Mono<ResponseEntity> run(@PathVariable String year, @PathVariable Boolean truncateDestinationTable) throws Exception {
        migrationDocumentXmlService.run(year, truncateDestinationTable);
        return Mono.empty();
    }
}
