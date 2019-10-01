package eu.nimble.service.bp.util.email;

import eu.nimble.common.rest.identity.IIdentityClientTyped;
import eu.nimble.service.bp.model.hyperjaxb.*;
import eu.nimble.service.bp.util.persistence.bp.ProcessDocumentMetadataDAOUtility;
import eu.nimble.service.model.ubl.commonaggregatecomponents.PartyType;
import eu.nimble.service.model.ubl.commonaggregatecomponents.PersonType;
import eu.nimble.utility.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suat on 16-Oct-18.
 */
@Component
public class EmailSenderUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String COLLABORATION_ROLE_BUYER = "BUYER";
    private final static String COLLABORATION_ROLE_SELLER = "SELLER";
    private final static String EMPTY_TEXT = "";
    private final static String URL_TEXT = "Link : ";

    @Autowired
    private EmailService emailService;

    @Autowired
    private IIdentityClientTyped iIdentityClientTyped;

    @Value("${nimble.frontend.url}")
    private String frontEndURL;

    // email sender thread
    public void sendCollaborationStatusEmail(String bearerToken, ProcessInstanceGroupDAO groupDAO) {
        new Thread(() -> {
            // Collect the trading partner name
            String partyId = groupDAO.getPartyID();
            PartyType tradingPartner;
            String tradingPartnerId = ProcessDocumentMetadataDAOUtility.getTradingPartnerId(groupDAO.getProcessInstanceIDs().get(0), partyId);
            try {
                tradingPartner = iIdentityClientTyped.getParty(bearerToken, tradingPartnerId);

            } catch (IOException e) {
                logger.error("Failed to send email for group: {} with status: {}", groupDAO.getID(), groupDAO.getStatus().toString());
                logger.error("Failed to get party with id: {} from identity service", tradingPartnerId, e);
                return;
            }

            // collect product name
            List<String> productNameList = ProcessDocumentMetadataDAOUtility.findByProcessInstanceID(groupDAO.getProcessInstanceIDs().get(0)).get(0).getRelatedProducts();
            StringBuilder productNames = new StringBuilder("");
            for (int i = 0; i < productNameList.size() - 1; i++) {
                productNames.append(productNameList.get(i)).append(", ");
            }
            productNames.append(productNameList.get(productNameList.size() - 1));


            // Try to identify the recipient email
            //
            // If there are more than one instances in the group, simply select the first process instance and
            // use the creatorID of the document created by the trading party. Otherwise, use the email of the first
            // person associated with the trading partner
            String toEmail;
            if (groupDAO.getProcessInstanceIDs().size() > 1) {
                ProcessDocumentMetadataDAO documentMetadataDAO = ProcessDocumentMetadataDAOUtility.getDocumentOfTheOtherParty(groupDAO.getProcessInstanceIDs().get(0), partyId);
                // get person via the identity client
                String personId = documentMetadataDAO.getCreatorUserID();
                PersonType person;
                try {
                    person = iIdentityClientTyped.getPerson(bearerToken, personId);
                    toEmail = person.getContact().getElectronicMail();
                } catch (IOException e) {
                    logger.error("Failed to send email for group: {} with status: {}", groupDAO.getID(), groupDAO.getStatus().toString());
                    logger.error("Failed to get person with id: {} from identity service", personId, e);
                    return;
                }

            } else {
                toEmail = tradingPartner.getPerson().get(0).getContact().getElectronicMail();
            }

            // collect name of the person cancelling the collaboration
            PersonType person;
            String personName;
            try {
                person = iIdentityClientTyped.getPerson(bearerToken);

            } catch (IOException e) {
                logger.error("Failed to send email for group: {} with status: {}", groupDAO.getID(), groupDAO.getStatus().toString());
                logger.error("Failed to get person with token: {} from identity service", bearerToken, e);
                return;
            }
            personName = new StringBuilder("").append(person.getFirstName()).append(" ").append(person.getFamilyName()).toString();

            notifyPartyOnCollaborationStatus(toEmail, personName, productNames.toString(), tradingPartner.getPartyName().get(0).getName().getValue(),groupDAO.getStatus());
            logger.info("Collaboration status mail sent to: {} for group: {} with status: {}", toEmail, groupDAO.getID(), groupDAO.getStatus().toString());
        }).start();
    }

    public void notifyPartyOnCollaborationStatus(String toEmail, String tradingPartnerPersonName, String productName, String tradingPartnerName, GroupStatus status){
        Context context = new Context();
        String subject;
        String template;

        context.setVariable("tradingPartnerPerson", tradingPartnerPersonName);
        context.setVariable("tradingPartner", tradingPartnerName);
        context.setVariable("product", productName);

        if(status.equals(GroupStatus.CANCELLED)){
            subject = "NIMBLE: Business process cancelled";
            template = "cancelled_collaboration";
        }
        else{
            subject = "NIMBLE: Business process finished";
            template = "finished_collaboration";
        }

        emailService.send(new String[]{toEmail}, subject, template, context);
    }

    public void sendActionPendingEmail(String bearerToken, String documentId) {
        new Thread(() -> {
            // Get ProcessDocumentMetadataDAO for the given document id
            ProcessDocumentMetadataDAO processDocumentMetadataDAO = ProcessDocumentMetadataDAOUtility.findByDocumentID(documentId);
            // if negotiation awaits response
            ProcessDocumentStatus processDocumentStatus = processDocumentMetadataDAO.getStatus();
            List<String> emailList = new ArrayList<>();
            PartyType respondingParty;
            PartyType initiatingParty;
            PartyType sellerParty = null;
            PartyType buyerParty = null;
            PersonType initiatingPerson;
            String respondingPartyName;
            String initiatingPartyName;
            String subject = EMPTY_TEXT;
            boolean showURL = true;
            boolean initiatorIsBuyer = true;

            try {
                if (processDocumentStatus.equals(ProcessDocumentStatus.WAITINGRESPONSE)) {
                    respondingParty = iIdentityClientTyped.getParty(bearerToken, processDocumentMetadataDAO.getResponderID());
                    initiatingParty = iIdentityClientTyped.getParty(bearerToken, processDocumentMetadataDAO.getInitiatorID());
                }else {
                    respondingParty = iIdentityClientTyped.getParty(bearerToken, processDocumentMetadataDAO.getInitiatorID());
                    initiatingParty = iIdentityClientTyped.getParty(bearerToken, processDocumentMetadataDAO.getResponderID());
                }

            } catch (IOException e) {
                logger.error("Failed to get party with id: {} from identity service", processDocumentMetadataDAO.getResponderID(), e);
                return;
            }

            try {
                initiatingPerson = iIdentityClientTyped.getPerson(bearerToken);
            } catch (IOException e) {
                logger.error("Failed to get person with token: {} from identity service", bearerToken, e);
                return;
            }

            String initiatingPersonName = new StringBuilder("").append(initiatingPerson.getFirstName()).append(" ").append(initiatingPerson.getFamilyName()).toString();
            String productName = processDocumentMetadataDAO.getRelatedProducts().get(0);

            List<PersonType> personTypeList = respondingParty.getPerson();
            for (PersonType p : personTypeList) {
                emailList.add(p.getContact().getElectronicMail());
            }
            respondingPartyName = respondingParty.getPartyName().get(0).getName().getValue();
            initiatingPartyName = initiatingParty.getPartyName().get(0).getName().getValue();

            DocumentType documentType = processDocumentMetadataDAO.getType();
            if (documentType.equals(DocumentType.ITEMINFORMATIONREQUEST)) {
                subject = "NIMBLE: Information Requested for " + productName + " from " + initiatingPartyName;
            }else if(documentType.equals(DocumentType.REQUESTFORQUOTATION)) {
                subject = "NIMBLE: Quotation Requested for " + productName + " from " + initiatingPartyName;
            }else if(documentType.equals(DocumentType.ORDER)) {
                subject = "NIMBLE: Order Received for " + productName + " from " + initiatingPartyName;
            }else if(documentType.equals(DocumentType.RECEIPTADVICE)) {
                subject = "NIMBLE: Receipt Advice Received for " + productName + " from " + initiatingPartyName;
            }else if (processDocumentMetadataDAO.getType().equals(DocumentType.ITEMINFORMATIONRESPONSE)){
                initiatorIsBuyer = false;
                subject = "NIMBLE: Information Received for " + productName + " from " + initiatingPartyName;
            }else if (processDocumentMetadataDAO.getType().equals(DocumentType.QUOTATION)){
                initiatorIsBuyer = false;
                subject = "NIMBLE: Quotation Received for " + productName + " from " + initiatingPartyName;
            } else if (processDocumentMetadataDAO.getType().equals(DocumentType.ORDERRESPONSESIMPLE)){
                initiatorIsBuyer = false;
                subject = "NIMBLE: Order Response for " + productName + " from " + initiatingPartyName;
            }else if (processDocumentMetadataDAO.getType().equals(DocumentType.DESPATCHADVICE)){
                initiatorIsBuyer = false;
                subject = "NIMBLE: Dispatch Advice Received for " + productName + " from " + initiatingPartyName;
            }else {
                showURL = false;
            }

            if (initiatorIsBuyer) {
                buyerParty = initiatingParty;
                sellerParty = respondingParty;
            }else {
                buyerParty = respondingParty;
                sellerParty = initiatingParty;
            }

            String url = EMPTY_TEXT;

            if (showURL) {
                if (processDocumentMetadataDAO.getResponderID().equals(String.valueOf(sellerParty.getPartyIdentification().get(0).getID()))) {
                    if (processDocumentStatus.equals(ProcessDocumentStatus.WAITINGRESPONSE)) {
                        url = getPendingActionURL(COLLABORATION_ROLE_SELLER);
                    }else {
                        url = getPendingActionURL(COLLABORATION_ROLE_BUYER);
                    }
                } else if (processDocumentMetadataDAO.getResponderID().equals(String.valueOf(buyerParty.getPartyIdentification().get(0).getID()))) {
                    if (processDocumentStatus.equals(ProcessDocumentStatus.WAITINGRESPONSE)) {
                        url = getPendingActionURL(COLLABORATION_ROLE_BUYER);
                    }else {
                        url = getPendingActionURL(COLLABORATION_ROLE_SELLER);
                    }
                }
            }

            if (processDocumentStatus.equals(ProcessDocumentStatus.WAITINGRESPONSE)) {
                notifyPartyOnPendingCollaboration(emailList.toArray(new String[0]), initiatingPersonName, productName, initiatingPartyName, url, subject, respondingPartyName);
            } else {
                notifyPartyOnCollaboration(emailList.toArray(new String[0]), initiatingPersonName, productName, initiatingPartyName, url, subject, respondingPartyName);
            }
        }).start();
    }

    private String getPendingActionURL(String collaborationRole) {
        if (COLLABORATION_ROLE_SELLER.equals(collaborationRole)) {
            return URL_TEXT + frontEndURL + "/#/dashboard?tab=SALES";
        } else if (COLLABORATION_ROLE_BUYER.equals(collaborationRole)) {
            return URL_TEXT + frontEndURL + "/#/dashboard?tab=PUCHASES";
        }
        return EMPTY_TEXT;
    }

    public void notifyPartyOnPendingCollaboration(String[] toEmail, String initiatingPersonName, String productName,
                                                  String initiatingPartyName, String url, String subject, String respondingPartyName) {
        Context context = new Context();

        context.setVariable("initiatingPersonName", initiatingPersonName);
        context.setVariable("initiatingPartyName", initiatingPartyName);
        context.setVariable("respondingPartyName", respondingPartyName);
        context.setVariable("product", productName);

        if (!url.isEmpty()) {
            context.setVariable("url", url);
        }

        if (subject.equals(EMPTY_TEXT)) {
            subject = "NIMBLE: Action Required for the Business Process";
        }

        emailService.send(toEmail, subject, "action_pending", context);
    }

    public void notifyPartyOnCollaboration(String[] toEmail, String initiatingPersonName, String productName, String initiatingPartyName,
                                           String url, String subject, String respondingPartyName) {
        Context context = new Context();

        context.setVariable("initiatingPersonName", initiatingPersonName);
        context.setVariable("initiatingPartyName", initiatingPartyName);
        context.setVariable("respondingPartyName", respondingPartyName);
        context.setVariable("product", productName);

        if (!url.isEmpty()) {
            context.setVariable("url", url);
        }

        if (subject.equals(EMPTY_TEXT)) {
            subject = "NIMBLE: Transition of the Business Process";
        }

        emailService.send(toEmail, subject, "continue_colloboration", context);
    }
}