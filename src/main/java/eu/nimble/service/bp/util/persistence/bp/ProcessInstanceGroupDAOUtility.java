package eu.nimble.service.bp.util.persistence.bp;

import eu.nimble.service.bp.model.hyperjaxb.CollaborationGroupDAO;
import eu.nimble.service.bp.model.hyperjaxb.GroupStatus;
import eu.nimble.service.bp.model.hyperjaxb.ProcessInstanceDAO;
import eu.nimble.service.bp.model.hyperjaxb.ProcessInstanceGroupDAO;
import eu.nimble.utility.HibernateUtility;
import eu.nimble.utility.persistence.GenericJPARepository;
import eu.nimble.utility.persistence.JPARepositoryFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by suat on 26-Mar-18.
 */
public class ProcessInstanceGroupDAOUtility {

    private static final String QUERY_GET_PROCESS_INSTANCE_GROUPS =
            "SELECT pig, max(doc.submissionDate) AS lastActivityTime, min(doc.submissionDate) AS firstActivityTime FROM" +
                    " ProcessInstanceGroupDAO pig JOIN pig.processInstanceIDsItems pid," +
                    " ProcessInstanceDAO pi," +
                    " ProcessDocumentMetadataDAO doc" +
                    " WHERE" +
                    " ( pig.ID = :groupId) AND" +
                    " pid.item = pi.processInstanceID AND" +
                    " doc.processInstanceID = pi.processInstanceID" +
                    " GROUP BY pig.hjid";

    private static final String QUERY_GET_ORDER_ID_IN_GROUP =
            "SELECT DISTINCT doc.documentID FROM ProcessInstanceGroupDAO pig join pig.processInstanceIDsItems pid," +
                    " ProcessInstanceDAO pi, " +
                    " ProcessDocumentMetadataDAO doc" +
                    " WHERE " +
                    " pid.item = pi.processInstanceID AND" +
                    " doc.processInstanceID = pi.processInstanceID AND" +
                    " doc.type = 'ORDER' AND pig.ID IN" +

                    " (" +
                    " SELECT pig2.ID FROM ProcessInstanceGroupDAO pig2 join pig2.processInstanceIDsItems pid2," +
                    " ProcessInstanceDAO pi2 " +
                    " WHERE" +
                    " pid2.item = pi2.processInstanceID AND " +
                    " pi2.processInstanceID = :processInstanceId" +
                    ")";

    private static final String QUERY_GET_BY_ASSOCIATED_GROUP_ID =
            "select pig from ProcessInstanceGroupDAO pig join pig.processInstanceIDsItems pid where pig.partyID = :partyId and pid.item in :pids";

    private static final String QUERY_GET_ASSOCIATED_GROUPS =
            "select pig from ProcessInstanceGroupDAO pig join pig.processInstanceIDsItems pid where pig.partyID <> :partyId and pid.item in :pids";

    private static final String QUERY_GET_PRECEDING_PROCESS_INSTANCE_GROUP =
            "SELECT pig.precedingProcessInstanceGroup FROM ProcessInstanceGroupDAO pig join pig.processInstanceIDsItems pid," +
                    "ProcessInstanceDAO pi " +
                    "WHERE " +
                    "pid.item = pi.processInstanceID AND " +
                    "pi.processInstanceID = :processInstanceId AND pig.precedingProcessInstanceGroup IS NOT NULL";

    private static final String QUERY_GET_CONTAINING_THE_PROCESS =
            "SELECT pig FROM ProcessInstanceGroupDAO pig join pig.processInstanceIDsItems pid WHERE pid.item = :processInstanceID";

    private static final String QUERY_GET_BY_PARTY_ID = "SELECT pig.ID FROM ProcessInstanceGroupDAO pig WHERE pig.partyID in :partyIds";

    public static List<String> getProcessInstanceGroupIdsForParty(List<String> partyIds){
        return new JPARepositoryFactory().forBpRepository().getEntities(QUERY_GET_BY_PARTY_ID, new String[]{"partyIds"}, new Object[]{partyIds});
    }

    public static Object getProcessInstanceGroups(String groupId, GenericJPARepository repository) {
        return repository.getSingleEntity(QUERY_GET_PROCESS_INSTANCE_GROUPS, new String[]{"groupId"}, new Object[]{groupId});
    }

    public static ProcessInstanceGroupDAO getProcessInstanceGroupDAO(String partyId, List<String> processInstanceIds, GenericJPARepository genericJPARepository) {
        return genericJPARepository.getSingleEntity(QUERY_GET_BY_ASSOCIATED_GROUP_ID, new String[]{"partyId", "pids"}, new Object[]{partyId, processInstanceIds});
    }

    public static ProcessInstanceGroupDAO getProcessInstanceGroupDAO(String partyId, List<String> processInstanceIds) {
        return getProcessInstanceGroupDAO(partyId, processInstanceIds, new JPARepositoryFactory().forBpRepository(true));
    }

    /**
     * This method is used to retrieve process instance groups which contain the given process instance id.
     */
    public static List<ProcessInstanceGroupDAO> getProcessInstanceGroupDAOs(String processInstanceId, GenericJPARepository repository){
        return repository.getEntities(QUERY_GET_CONTAINING_THE_PROCESS, new String[]{"processInstanceID"}, new Object[]{processInstanceId});
    }

    public static List<ProcessInstanceGroupDAO> getAssociatedProcessInstanceGroupDAOs(String partyId, List<String> processInstanceIds){
        return new JPARepositoryFactory().forBpRepository(true).getEntities(QUERY_GET_ASSOCIATED_GROUPS, new String[]{"partyId", "pids"}, new Object[]{partyId, processInstanceIds});
    }

    public static ProcessInstanceGroupDAO getPrecedingProcessInstanceGroup(String processInstanceId) {
        return new JPARepositoryFactory().forBpRepository(true).getSingleEntity(QUERY_GET_PRECEDING_PROCESS_INSTANCE_GROUP, new String[]{"processInstanceId"}, new Object[]{processInstanceId});
    }

    public static ProcessInstanceGroupDAO createProcessInstanceGroupDAO(String partyId, String processInstanceId, String collaborationRole, List<String> relatedProducts,GenericJPARepository repo) {
        return createProcessInstanceGroupDAO(partyId, processInstanceId, collaborationRole, relatedProducts, null,repo);
    }

    public static ProcessInstanceGroupDAO createProcessInstanceGroupDAO(String partyId, String processInstanceId, String collaborationRole, List<String> relatedProducts, String dataChannelId, GenericJPARepository repo) {
        String uuid = UUID.randomUUID().toString();
        ProcessInstanceGroupDAO group = new ProcessInstanceGroupDAO();
        group.setArchived(false);
        group.setID(uuid);
        String groupName = "";
        for (int i = 0; i < relatedProducts.size(); i++) {
            if (i == relatedProducts.size() - 1) {
                groupName += relatedProducts.get(i);
            } else {
                groupName += relatedProducts.get(i) + ",";
            }
        }
        group.setName(groupName);
        group.setPartyID(partyId);
        group.setStatus(GroupStatus.INPROGRESS);
        group.setDataChannelId(dataChannelId);
        group.setCollaborationRole(collaborationRole);
        List<String> processInstanceIds = new ArrayList<>();
        processInstanceIds.add(processInstanceId);
        group.setProcessInstanceIDs(processInstanceIds);
        repo.persistEntity(group);
        return group;
    }

    public static ProcessInstanceGroupDAO getProcessInstanceGroupDAO(String groupID, GenericJPARepository repository) {
        Object[] resultItems = (Object[]) getProcessInstanceGroups(groupID,repository);
        // return null if there is no process instance group with the given id
        if(resultItems == null){
            return null;
        }
        ProcessInstanceGroupDAO pig = (ProcessInstanceGroupDAO) resultItems[0];
        pig.setLastActivityTime((String) resultItems[1]);
        pig.setFirstActivityTime((String) resultItems[2]);
        return pig;
    }

    public static ProcessInstanceGroupDAO getProcessInstanceGroupDAO(String groupID) {
        return getProcessInstanceGroupDAO(groupID,new JPARepositoryFactory().forBpRepository(true));
    }

    public static ProcessInstanceDAO getProcessInstance(String processInstanceId) {
        String queryStr = "SELECT pi FROM ProcessInstanceDAO pi WHERE pi.processInstanceID = ?";
        ProcessInstanceDAO pi = HibernateUtility.getInstance("bp-data-model").load(queryStr, processInstanceId);
        return pi;
    }

    public static void deleteProcessInstanceGroupDAOByID(String groupID) {
        CollaborationGroupDAO collaborationGroupDAO = CollaborationGroupDAOUtility.getCollaborationGroupOfProcessInstanceGroup(groupID);
        // if the collaboration group only contains the given group,then delete the collaboration group so that there will not be any garbage on the database
        if (collaborationGroupDAO.getAssociatedProcessInstanceGroups().size() == 1) {
            CollaborationGroupDAOUtility.deleteCollaborationGroupDAOsByID(Collections.singletonList(collaborationGroupDAO.getHjid()));
        } else {
            for (ProcessInstanceGroupDAO groupDAO : collaborationGroupDAO.getAssociatedProcessInstanceGroups()) {
                if (groupDAO.getID().equals(groupID)) {
                    GenericJPARepository repo = new JPARepositoryFactory().forBpRepository();
                    repo.deleteEntityByHjid(ProcessInstanceGroupDAO.class, groupDAO.getHjid());
                    return;
                }
            }
        }
    }

    public static String getOrderIdInGroup(String processInstanceId) {
        // get the preceding process instance group if there is any
        ProcessInstanceGroupDAO precedingProcessInstanceGroup = ProcessInstanceGroupDAOUtility.getPrecedingProcessInstanceGroup(processInstanceId);
        String orderId;
        if (precedingProcessInstanceGroup != null) {
            orderId = new JPARepositoryFactory().forBpRepository().getSingleEntity(QUERY_GET_ORDER_ID_IN_GROUP, new String[]{"processInstanceId"}, new Object[]{precedingProcessInstanceGroup.getProcessInstanceIDs().get(0)});
        } else {
            orderId = new JPARepositoryFactory().forBpRepository().getSingleEntity(QUERY_GET_ORDER_ID_IN_GROUP, new String[]{"processInstanceId"}, new Object[]{processInstanceId});
        }
        return orderId;
    }

    private enum GroupQueryType {
        GROUP, FILTER, SIZE
    }

    private static class QueryData {
        private String query;
        private List<String> parameterNames = new ArrayList<>();
        private List<Object> parameterValues = new ArrayList<>();
    }
}