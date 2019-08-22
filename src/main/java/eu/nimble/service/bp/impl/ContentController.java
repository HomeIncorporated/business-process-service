package eu.nimble.service.bp.impl;

import eu.nimble.service.bp.model.hyperjaxb.ProcessDAO;
import eu.nimble.service.bp.util.HttpResponseUtil;
import eu.nimble.service.bp.util.camunda.CamundaEngine;
import eu.nimble.service.bp.util.jssequence.JSSequenceDiagramParser;
import eu.nimble.service.bp.util.persistence.bp.HibernateSwaggerObjectMapper;
import eu.nimble.service.bp.util.persistence.bp.ProcessDAOUtility;
import eu.nimble.service.bp.util.spring.SpringBridge;
import eu.nimble.service.bp.swagger.api.ContentApi;
import eu.nimble.service.bp.swagger.model.ModelApiResponse;
import eu.nimble.service.bp.swagger.model.Process;
import eu.nimble.utility.persistence.JPARepositoryFactory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yildiray on 5/25/2017.
 */
public class ContentController implements ContentApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JPARepositoryFactory repoFactory;

    @Override
    @ApiOperation(value = "",notes = "Add a new business process")
    public ResponseEntity<ModelApiResponse> addProcessDefinition(@ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken,
                                                                 @RequestBody Process body
    ) {
        logger.info(" $$$ Adding business process definition: ");
        logger.debug(" $$$ {}", body.toString());

        ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }

        String bpmnContent = body.getBpmnContent();
        if (bpmnContent == null || bpmnContent.trim().equals("")) {
            logger.info(" $$$ BPMN Content is empty. Hence BPMN is created from the text content...");
            JSSequenceDiagramParser parser = new JSSequenceDiagramParser(body);
            bpmnContent = parser.getBPMNContent();
            logger.debug(" $$$ Generated BPMN Content: \n {}", bpmnContent);
            body.setTransactions(parser.getTransactions());
        }

        CamundaEngine.addProcessDefinition(body.getProcessID(), bpmnContent);

        ProcessDAO processDAO = HibernateSwaggerObjectMapper.createProcess_DAO(body);
        repoFactory.forBpRepository().persistEntity(processDAO);

        return HibernateSwaggerObjectMapper.getApiResponse();
    }

    @Override
    @ApiOperation(value = "",notes = "Delete a business process definition")
    public ResponseEntity<ModelApiResponse> deleteProcessDefinition(@ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken,
                                                                    @PathVariable("processID") String processID) {
        logger.info(" $$$ Deleting business process definition for ... {}", processID);
        ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }
        CamundaEngine.deleteProcessDefinition(processID);

        ProcessDAO processDAO = ProcessDAOUtility.findByProcessID(processID);
        if(processDAO != null)
            repoFactory.forBpRepository().deleteEntityByHjid(ProcessDAO.class, processDAO.getHjid());

        return HibernateSwaggerObjectMapper.getApiResponse();
    }

    @Override
    @ApiOperation(value = "",notes = "Get the business process definitions")
    public ResponseEntity<Process> getProcessDefinition(@ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken,
                                                        @PathVariable("processID") String processID) {
        logger.info(" $$$ Getting business process definition for ... {}", processID);
        ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }
        ProcessDAO processDAO = ProcessDAOUtility.findByProcessID(processID);
        // The process definition is not in the database...
        Process process = null;
        if (processDAO != null)
            process = HibernateSwaggerObjectMapper.createProcess(processDAO);
        else
            process = CamundaEngine.getProcessDefinition(processID);

        logger.debug(" $$$ Returning process definition {}", process.toString());
        return new ResponseEntity<>(process, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "",notes = "Get the business process definitions")
    public ResponseEntity<List<Process>> getProcessDefinitions(@ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken
    ) {
        logger.info(" $$$ Getting business process definitions");
        ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }

        // first get the ones in the database
        List<ProcessDAO> processDAOs = ProcessDAOUtility.getProcessDAOs();
        List<Process> processes = new ArrayList<>();
        for (ProcessDAO processDAO : processDAOs) {
            Process process = HibernateSwaggerObjectMapper.createProcess(processDAO);
            processes.add(process);
        }
        // then get the ones initially loaded
        List<Process> defaultProcesses = CamundaEngine.getProcessDefinitions();
        // merge them
        for(Process defaultProcess: defaultProcesses) {
            boolean found = false;
            for(Process process: processes) {
                if(process.getProcessID().equals(defaultProcess.getProcessID())) {
                    found = true;
                    break;
                }
            }
            if(!found)
                processes.add(defaultProcess);
        }

        return new ResponseEntity<>(processes, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "",notes = "Update a business process")
    public ResponseEntity<ModelApiResponse> updateProcessDefinition(@ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken,
                                                                    @RequestBody Process body) {
        logger.info(" $$$ Updating business process definition: ");
        logger.debug(" $$$ {}", body.toString());

        ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }

        String bpmnContent = body.getBpmnContent();
        if (bpmnContent == null || bpmnContent.trim().equals("")) {
            logger.info(" $$$ BPMN Content is empty. Hence BPMN is created from the text content...");
            JSSequenceDiagramParser parser = new JSSequenceDiagramParser(body);
            bpmnContent = parser.getBPMNContent();
            body.setTransactions(parser.getTransactions());
        }

        CamundaEngine.updateProcessDefinition(body.getProcessID(), bpmnContent);

        ProcessDAO processDAO = ProcessDAOUtility.findByProcessID(body.getProcessID());
        ProcessDAO processDAONew = HibernateSwaggerObjectMapper.createProcess_DAO(body);

        processDAONew.setHjid(processDAO.getHjid());

        repoFactory.forBpRepository().updateEntity(processDAONew);

        return HibernateSwaggerObjectMapper.getApiResponse();
    }
}
