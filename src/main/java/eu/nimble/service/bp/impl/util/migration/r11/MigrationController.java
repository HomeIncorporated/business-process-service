package eu.nimble.service.bp.impl.util.migration.r11;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.nimble.service.bp.impl.util.migration.r11.serialization.BinaryObjectSerializerGetBinaryObjects;
import eu.nimble.service.bp.impl.util.spring.SpringBridge;
import eu.nimble.service.model.ubl.catalogue.CatalogueType;
import eu.nimble.service.model.ubl.commonbasiccomponents.BinaryObjectType;
import eu.nimble.utility.JsonSerializationUtility;
import eu.nimble.utility.persistence.GenericJPARepository;
import eu.nimble.utility.persistence.JPARepositoryFactory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@ApiIgnore
@Controller
public class MigrationController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String QUERY_GET_BINARY_OBJECTS = "FROM BinaryObjectType binaryObject WHERE binaryObject.uri LIKE :uri";

    private final String CATALOG_BINARY_CONTENT_URI = "CatalogBinaryContentUri:";
    private final String BUSINESS_PROCESS_BINARY_CONTENT_URI = "BusinessProcessBinaryContentUri:";

    @ApiOperation(value = "",notes = "Updates the uri of binary objects whose uris start with the given uri.The given uri should be the value of BINARY_CONTENT_URL environment variable." +
            " Firstly, it retrieves such binary objects belonging to the catalogues and updates their uris to CatalogBinaryContentUri:UUID. For the rest of them, it updates their uris" +
            " to BusinessProcessBinaryContentUri:UUID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated binary content uris successfully"),
            @ApiResponse(code = 401, message = "Invalid token. No user was found for the provided token"),
            @ApiResponse(code = 500, message = "Unexpected error while updating binary content uris")
    })
    @RequestMapping(value = "/migration/binarycontents",
            produces = {"application/json"},
            method = RequestMethod.PATCH)
    public ResponseEntity updateBinaryContents(@ApiParam(value = "Value of BINARY_CONTENT_URL environment variable.",required = true) @RequestParam(value = "previousUri",required = true) String previousUri,
                                               @ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken
    ) {
        logger.info("Incoming request to update binary content uris");

        // check token
        ResponseEntity tokenCheck = eu.nimble.service.bp.impl.util.HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }

        GenericJPARepository repo = new JPARepositoryFactory().forCatalogueRepository(true);
        // get catalogues
        String GET_CATALOGUES = "FROM CatalogueType";
        try{
            List<String> urisToBeDeleted = new ArrayList<>();
            List<CatalogueType> catalogues = repo.getEntities(GET_CATALOGUES);
            logger.info("Retrieved catalogues");
            // get catalogue binary content uris
            List<BinaryObjectType> catalogBinaryObjects = getCatalogBinaryObjects(catalogues);
            logger.info("Retrieved catalogue binary objects");
            // get uris to be deleted
            urisToBeDeleted.addAll(getBinaryObjectUris(catalogBinaryObjects,previousUri));
            // update catalog binary contents
            updateBinaryContentUris(catalogBinaryObjects, CATALOG_BINARY_CONTENT_URI,previousUri);
            logger.info("Updated uris of catalogue binary objects");
            // get binary objects
            List<BinaryObjectType> bpBinaryObjects = getBinaryObjects(previousUri);
            logger.info("Retrieved business process binary objects");
            // get uris to be deleted
            urisToBeDeleted.addAll(getBinaryObjectUris(bpBinaryObjects,previousUri));
            // update bp binary contents
            updateBinaryContentUris(bpBinaryObjects, BUSINESS_PROCESS_BINARY_CONTENT_URI,previousUri);
            logger.info("Updated uris of business process binary objects");
            // delete old binary contents
            SpringBridge.getInstance().getBinaryContentService().deleteContents(urisToBeDeleted);
            logger.info("Deleted binary contents with old uris");
        }
        catch (Exception e){
            logger.error("Unexpected error while updating binary content uris",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error while updating binary content uris");
        }
        logger.info("Completed request to update binary content uris");
        return ResponseEntity.ok(null);
    }

    private List<String> getBinaryObjectUris(List<BinaryObjectType> binaryObjects,String uriPart){
        List<String> uris = new ArrayList<>();
        for(BinaryObjectType binaryObject:binaryObjects){
            if(binaryObject.getUri().contains(uriPart)){
                uris.add(binaryObject.getUri());
            }
        }
        return uris;
    }

    private List<BinaryObjectType> getCatalogBinaryObjects(List<CatalogueType> catalogues){
        ObjectMapper objectMapper = JsonSerializationUtility.getObjectMapper();
        BinaryObjectSerializerGetBinaryObjects serializer = new BinaryObjectSerializerGetBinaryObjects();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BinaryObjectType.class, serializer);
        objectMapper.registerModule(simpleModule);

        try {
            objectMapper.writeValueAsString(catalogues);

        } catch (JsonProcessingException e) {
            String msg = String.format("Failed to serialize object: %s", catalogues.getClass().getName());
            logger.error(msg);
            throw new RuntimeException(msg, e);
        }
        return serializer.getObjects();
    }

    public List<BinaryObjectType> getBinaryObjects(String uriPart){
        return new JPARepositoryFactory().forCatalogueRepository().getEntities(QUERY_GET_BINARY_OBJECTS,new String[]{"uri"}, new Object[]{"%"+uriPart+"%"});
    }

    public void updateBinaryContentUris(List<BinaryObjectType> binaryObjects, String newUrl, String oldUrl){
        GenericJPARepository repo = new JPARepositoryFactory().forCatalogueRepository();
        for(BinaryObjectType binaryObject: binaryObjects){

            if(binaryObject.getUri().contains(oldUrl)){
                // get binary content
                BinaryObjectType binaryContent = SpringBridge.getInstance().getBinaryContentService().retrieveContent(binaryObject.getUri());
                if(binaryContent != null){
                    // create a new content
                    BinaryObjectType newBinaryContent = new BinaryObjectType();
                    newBinaryContent.setValue(binaryContent.getValue());
                    newBinaryContent.setMimeCode(binaryContent.getMimeCode());
                    newBinaryContent.setFileName(binaryContent.getFileName());
                    newBinaryContent.setUri(binaryContent.getUri().replace(oldUrl,newUrl));

                    if(SpringBridge.getInstance().getBinaryContentService().retrieveContent(newBinaryContent.getUri()) == null){
                        SpringBridge.getInstance().getBinaryContentService().createContent(newBinaryContent,false);
                    }
                }

                // update uri of binary object
                binaryObject.setUri(binaryObject.getUri().replace(oldUrl,newUrl));
                repo.updateEntity(binaryObject);
            }

        }
        logger.info("Uris of binary contents are updated to the new url: {}",newUrl);
    }

}