package eu.nimble.service.bp.impl;

import eu.nimble.service.bp.model.hyperjaxb.CollaborationGroupDAO;
import eu.nimble.service.bp.model.hyperjaxb.ProcessInstanceGroupDAO;
import eu.nimble.service.bp.model.hyperjaxb.ProcessInstanceStatus;
import eu.nimble.service.bp.model.dashboard.CollaborationGroupResponse;
import eu.nimble.service.bp.util.HttpResponseUtil;
import eu.nimble.service.bp.util.persistence.bp.CollaborationGroupDAOUtility;
import eu.nimble.service.bp.util.persistence.bp.HibernateSwaggerObjectMapper;
import eu.nimble.service.bp.swagger.api.CollaborationGroupsApi;
import eu.nimble.service.bp.swagger.model.CollaborationGroup;
import eu.nimble.utility.persistence.GenericJPARepository;
import eu.nimble.utility.persistence.JPARepositoryFactory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * {@link CollaborationGroupDAO}s are entities that keep track collaboration activities with several companies related to
 * an individual product. Collaborations with distinct companies are kept in {@link ProcessInstanceGroupDAO}s.
 * In this sense, considering a simple example, {@link CollaborationGroupDAO} of a seller might contain two {@link ProcessInstanceGroupDAO}s
 * such that the first one contains seller-buyer activities and the second one seller-transport service provider activities.
 */
@Controller
public class CollaborationGroupsController implements CollaborationGroupsApi{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JPARepositoryFactory repoFactory;

    @Override
    @ApiOperation(value = "", notes = "Archives the collaboration group by setting the archive field of the specified CollaborationGroup and the included ProcessInstanceGroups.")
    public ResponseEntity archiveCollaborationGroup(@ApiParam(value = "Identifier of the collaboration group to be archived (collaborationGroup.hjid)", required = true) @PathVariable("id") String id,
                                                                        @ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken
    ) {
        logger.debug("Archiving CollaborationGroup: {}", id);
        try {
            // check token
            ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
            if (tokenCheck != null) {
                return tokenCheck;
            }

            // check whether the group is archiable or not
            boolean isArchivable = CollaborationGroupDAOUtility.isCollaborationGroupArchivable(Long.parseLong(id));
            if (!isArchivable) {
                String msg = String.format("CollaborationGroup with id %s is not archivable", id);
                logger.info(msg);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(msg);
            }

            CollaborationGroupDAO collaborationGroupDAO = CollaborationGroupDAOUtility.archiveCollaborationGroup(id);

            CollaborationGroup collaborationGroup = HibernateSwaggerObjectMapper.convertCollaborationGroupDAO(collaborationGroupDAO);
            ResponseEntity response = ResponseEntity.status(HttpStatus.OK).body(collaborationGroup);
            logger.debug("Archived CollaborationGroup: {}", id);
            return response;

        } catch (Exception e) {
            String msg = String.format("Unexpected error while archiving CollaborationGroup with id: %s", id);
            logger.error(msg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);        }
    }

    @Override
    @ApiOperation(value = "", notes = "Deletes the specified CollaborationGroup permanently.")
    public ResponseEntity<Void> deleteCollaborationGroup(@ApiParam(value = "Identifier of the collaboration group to be deleted (collaborationGroup.hjid)", required = true) @PathVariable("id") String id,
                                                         @ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken
    ) {
        logger.debug("Deleting CollaborationGroup ID: {}", id);
        // check token
        ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }


        CollaborationGroupDAOUtility.deleteCollaborationGroupDAOsByID(Collections.singletonList(Long.parseLong(id)));

        ResponseEntity response = ResponseEntity.status(HttpStatus.OK).body("true");
        logger.debug("Deleted CollaborationGroup ID: {}", id);
        return response;
    }

    @Override
    @ApiOperation(value = "", notes = "Retrieves the specified CollaborationGroup.")
    public ResponseEntity<CollaborationGroup> getCollaborationGroup(@ApiParam(value = "Identifier of the collaboration group to be received (collaborationGroup.hjid)", required = true) @PathVariable("id") String id,
                                                                    @ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken
    ) {
        logger.debug("Getting CollaborationGroup: {}", id);
        // check token
        ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }

        CollaborationGroupDAO collaborationGroupDAO = repoFactory.forBpRepository(true).getSingleEntityByHjid(CollaborationGroupDAO.class, Long.parseLong(id));
        if (collaborationGroupDAO == null) {
            logger.error("There does not exist a collaboration group with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        CollaborationGroup collaborationGroup = HibernateSwaggerObjectMapper.convertCollaborationGroupDAO(collaborationGroupDAO);
        ResponseEntity response = ResponseEntity.status(HttpStatus.OK).body(collaborationGroup);
        logger.debug("Retrieved CollaborationGroup: {}", id);
        return response;
    }

    @Override
    @ApiOperation(value = "", notes = "Retrieves CollaborationGroups based on the provided filter parameters. Some of the filter parameters (e.g. product names, trading partner ids) are sought in the inner ProcessInstanceGroups.")
    public ResponseEntity getCollaborationGroups(@ApiParam(value = "The Bearer token provided by the identity service", required = true) @RequestHeader(value = "Authorization", required = true) String bearerToken,
                                                                             @ApiParam(value = "Identifier of the party as specified by the identity service (party.id)") @RequestParam(value = "partyId", required = false) String partyId,
                                                                             @ApiParam(value = "Names of the products for which the collaboration activities are performed. If multiple product names to be provided, they should be comma-separated.") @RequestParam(value = "relatedProducts", required = false) List<String> relatedProducts,
                                                                             @ApiParam(value = "Categories of the products.<br>For example: MDF raw,Split air conditioner") @RequestParam(value = "relatedProductCategories", required = false) List<String> relatedProductCategories,
                                                                             @ApiParam(value = "Identifier (party id) of the corresponding trading partners") @RequestParam(value = "tradingPartnerIDs", required = false) List<String> tradingPartnerIDs,
                                                                             @ApiParam(value = "Offset of the first result among the complete result set satisfying the given criteria", defaultValue = "0") @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                                                             @ApiParam(value = "Number of results to be included in the result set", defaultValue = "10") @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                                                             @ApiParam(value = "Whether the collaboration group is archived or not", defaultValue = "false") @RequestParam(value = "archived", required = false, defaultValue = "false") Boolean archived,
                                                                             @ApiParam(value = "Status of the process instance included in the group.<br>Possible values:<ul><li>STARTED</li><li>WAITING</li><li>CANCELLED</li><li>COMPLETED</li></ul>") @RequestParam(value = "status", required = false) List<String> status,
                                                                             @ApiParam(value = "Role of the party in the collaboration.<br>Possible values:<ul><li>SELLER</li><li>BUYER</li></ul>") @RequestParam(value = "collaborationRole", required = false) String collaborationRole,
                                                                             @ApiParam(value = "Identify Project Or Not", defaultValue = "false") @RequestParam(value = "isProject", required = false, defaultValue = "false") Boolean isProject) {
        logger.debug("Getting collaboration groups for party: {}", partyId);
        try {
            List<CollaborationGroupDAO> results = CollaborationGroupDAOUtility.getCollaborationGroupDAOs(partyId, collaborationRole, archived, tradingPartnerIDs, relatedProducts, relatedProductCategories, status, null, null, limit, offset,isProject);
            int totalSize = CollaborationGroupDAOUtility.getCollaborationGroupSize(partyId, collaborationRole, archived, tradingPartnerIDs, relatedProducts, relatedProductCategories, status, null, null,isProject);
            logger.debug(" There are {} collaboration groups in total", results.size());
            List<CollaborationGroup> collaborationGroups = new ArrayList<>();
            List<Long> groupHjids = new ArrayList<>();
            for (CollaborationGroupDAO result : results) {
                collaborationGroups.add(HibernateSwaggerObjectMapper.convertCollaborationGroupDAO(result));
                groupHjids.add(result.getHjid());
            }

            CollaborationGroupResponse groupResponse = new CollaborationGroupResponse();
            groupResponse.setCollaborationGroups(collaborationGroups);
            groupResponse.setSize(totalSize);

            // get archiveable statuses
            Map<Long, Set<ProcessInstanceStatus>> archiveableStatuses = CollaborationGroupDAOUtility.getCollaborationProcessInstanceStatusesForCollaborationGroups(groupHjids);
            for(Map.Entry<Long, Set<ProcessInstanceStatus>> e : archiveableStatuses.entrySet()) {
                for(CollaborationGroup collaborationGroup : groupResponse.getCollaborationGroups()) {
                    if(e.getKey() == Long.parseLong(collaborationGroup.getID())) {
                        collaborationGroup.setIsArchiveable(CollaborationGroupDAOUtility.doesProcessInstanceStatusesMeetArchivingConditions(new ArrayList<>(e.getValue())));
                    }
                }
            }

            ResponseEntity response = ResponseEntity.status(HttpStatus.OK).body(groupResponse);
            logger.debug("Retrieved collaboration groups for party: {}", partyId);
            return response;

        } catch (Exception e) {
            String msg = String.format("Unexpected error while getting CollaborationGroups for party: %s", partyId);
            logger.error(msg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }
    }

    @Override
    @ApiOperation(value = "", notes = "Restores the specified, archived CollaborationGroup.")
    public ResponseEntity<CollaborationGroup> restoreCollaborationGroup(@ApiParam(value = "Identifier of the collaboration group to be restored (collaborationGroup.hjid)", required = true) @PathVariable("id") String id,
                                                                        @ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken
    ) {
        logger.debug("Restoring CollaborationGroup: {}", id);
        // check token
        ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }

        CollaborationGroupDAO collaborationGroupDAO = CollaborationGroupDAOUtility.restoreCollaborationGroup(id);

        CollaborationGroup collaborationGroup = HibernateSwaggerObjectMapper.convertCollaborationGroupDAO(collaborationGroupDAO);
        ResponseEntity response = ResponseEntity.status(HttpStatus.OK).body(collaborationGroup);
        logger.debug("Restored CollaborationGroup: {}", id);
        return response;
    }

    @Override
    @ApiOperation(value = "", notes = "Updates the name of the specified CollaborationGroup")
    public ResponseEntity<Void> updateCollaborationGroupName(@ApiParam(value = "Identifier of the collaboration group", required = true) @PathVariable("id") String id,
                                                             @ApiParam(value = "Value to be set as name of the collaboration group", required = true) @RequestParam(value = "groupName", required = true) String groupName,
                                                             @ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken
    ) {
        logger.debug("Updating name of the collaboration group :" + id);
        // check token
        ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }

        GenericJPARepository repo = repoFactory.forBpRepository();
        CollaborationGroupDAO collaborationGroupDAO = repo.getSingleEntityByHjid(CollaborationGroupDAO.class, Long.parseLong(id));
        if (collaborationGroupDAO == null) {
            String msg = String.format("There does not exist a collaboration group with id: %s", id);
            logger.error(msg);
            ResponseEntity response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            return response;
        }
        collaborationGroupDAO.setName(groupName);
        repo.updateEntity(collaborationGroupDAO);
        logger.debug("Updated name of the collaboration group :" + id);
        ResponseEntity response = ResponseEntity.status(HttpStatus.OK).body("true");
        return response;
    }



	@Override
    @ApiOperation(value = "", notes = "Merge list of CollaborationGroups")
    public ResponseEntity<CollaborationGroup> mergeCollaborationGroups(
            @ApiParam(value = "The Bearer token provided by the identity service" ,required=true ) @RequestHeader(value="Authorization", required=true) String bearerToken,
            @ApiParam(value = "Identifier of the base collaboration group ", required = true) @RequestParam("bcid") String bcid,
            @ApiParam(value = "List of collaboration group id's to be merged.", required = true) @RequestParam(value = "cgids", required = true) List<String> cgids

    ) {
        logger.debug("Merging the collaboration groups {} to the base collaboration group {}",cgids,bcid);
        // check token
        ResponseEntity tokenCheck = HttpResponseUtil.checkToken(bearerToken);
        if (tokenCheck != null) {
            return tokenCheck;
        }

        List<ProcessInstanceGroupDAO> allProcessInstanceGroups = new ArrayList<>();
        List<Long> allColabrationGroupList  = new ArrayList<>();

        GenericJPARepository repo = repoFactory.forBpRepository(true);
        CollaborationGroupDAO collaborationGroupDAO = repo.getSingleEntityByHjid(CollaborationGroupDAO.class, Long.parseLong(bcid));
        if (collaborationGroupDAO == null) {
            String msg = String.format("There does not exist a collaboration group with id: %s", bcid);
            logger.error(msg);
            ResponseEntity response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            return response;
        }

        allProcessInstanceGroups = collaborationGroupDAO.getAssociatedProcessInstanceGroups();
        allColabrationGroupList = collaborationGroupDAO.getAssociatedCollaborationGroups();

        for(String cgid : cgids){
            CollaborationGroupDAO mergeCollaborationGroupDAO = repo.getSingleEntityByHjid(CollaborationGroupDAO.class, Long.parseLong(cgid));
            if(mergeCollaborationGroupDAO != null) {
                allProcessInstanceGroups.addAll(mergeCollaborationGroupDAO.getAssociatedProcessInstanceGroups());
                allColabrationGroupList.addAll(mergeCollaborationGroupDAO.getAssociatedCollaborationGroups());
                for (Long mergeId:mergeCollaborationGroupDAO.getAssociatedCollaborationGroups()) {
                    CollaborationGroupDAO mergeCollaborationGroupDAOInstance = repo.getSingleEntityByHjid(CollaborationGroupDAO.class, mergeId);
                    List<Long> finalAssociatedInstanceIdList = mergeCollaborationGroupDAOInstance.getAssociatedCollaborationGroups();
                    finalAssociatedInstanceIdList.remove(Long.parseLong(cgid));
                    finalAssociatedInstanceIdList.add(Long.parseLong(bcid));
                    mergeCollaborationGroupDAOInstance.setAssociatedCollaborationGroups(finalAssociatedInstanceIdList);
                    repo.updateEntity(mergeCollaborationGroupDAOInstance);
                }
            }
        }

        collaborationGroupDAO.setAssociatedCollaborationGroups(allColabrationGroupList);
        collaborationGroupDAO.setAssociatedProcessInstanceGroups(allProcessInstanceGroups);
        collaborationGroupDAO.setIsProject(true);

        repo.updateEntity(collaborationGroupDAO);

        for(String cgid : cgids){
            CollaborationGroupDAOUtility.deleteCollaborationGroupDAOsByID(Collections.singletonList(Long.parseLong(cgid)));
        }
        collaborationGroupDAO.getAssociatedCollaborationGroups();

        CollaborationGroup collaborationGroup = HibernateSwaggerObjectMapper.convertCollaborationGroupDAO(collaborationGroupDAO);
        ResponseEntity response = ResponseEntity.status(HttpStatus.OK).body(collaborationGroup);
        logger.debug("Merged the collaboration groups {} to the base collaboration group {}",cgids,bcid);
        return response;
    }
}
