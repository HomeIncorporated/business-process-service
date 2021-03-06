package eu.nimble.service.bp.impl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.nimble.service.bp.model.dashboard.CollaborationGroupResponse;
import eu.nimble.service.bp.swagger.model.ProcessInstance;
import eu.nimble.service.bp.swagger.model.ProcessInstanceInputMessage;
import eu.nimble.utility.JsonSerializationUtility;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
public class ContinueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String orderResponseJSON1 = "/controller/orderResponseJSON1.txt";
    private final String orderResponseJSON2 = "/controller/orderResponseJSON2.txt";

    /**
     * Test scenario:
     * - Continue the first order process from the {@link StartControllerTest}
     * - Continue the second order process from the {@link StartControllerTest}
     * - Finish the collaboration to which the second order process from the {@link StartControllerTest} belongs
     */

    @Test
    public void test1_continueProcessInstance() throws Exception {
        ObjectMapper objectMapper = JsonSerializationUtility.getObjectMapper();
        String inputMessageAsString = IOUtils.toString(ProcessInstanceInputMessage.class.getResourceAsStream(orderResponseJSON1));
        inputMessageAsString = inputMessageAsString.replace("pid", StartControllerTest.processInstanceIdOrder1);

        // continue the process
        MockHttpServletRequestBuilder request = post("/continue")
                .header("Authorization", TestConfig.responderPersonId)
                .header("initiatorFederationId",TestConfig.federationId)
                .header("responderFederationId",TestConfig.federationId)
                .param("gid", StartControllerTest.sellerProcessInstanceGroupIdContainingOrder1)
                .param("collaborationGID", StartControllerTest.sellerCollaborationGroupIdContainingOrder1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputMessageAsString);
        MvcResult mvcResult = this.mockMvc.perform(request).andDo(print()).andExpect(status().isOk()).andReturn();

        ProcessInstance processInstance = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProcessInstance.class);
        Assert.assertEquals(processInstance.getStatus(), ProcessInstance.StatusEnum.COMPLETED);
        Assert.assertEquals(processInstance.getProcessInstanceID(), StartControllerTest.processInstanceIdOrder1);
    }

    @Test
    public void test2_continueProcessInstance() throws Exception {
        ObjectMapper objectMapper = JsonSerializationUtility.getObjectMapper();
        String inputMessageAsString = IOUtils.toString(ProcessInstanceInputMessage.class.getResourceAsStream(orderResponseJSON2));
        inputMessageAsString = inputMessageAsString.replace("pid", StartControllerTest.processInstanceIdOrder2);

        // continue the process
        MockHttpServletRequestBuilder request = post("/continue")
                .header("Authorization", TestConfig.responderPersonId)
                .header("initiatorFederationId",TestConfig.federationId)
                .header("responderFederationId",TestConfig.federationId)
                .param("gid", StartControllerTest.sellerProcessInstanceGroupIdContainingOrder2)
                .param("collaborationGID", StartControllerTest.sellerCollaborationGroupIdContainingOrder2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputMessageAsString);
        MvcResult mvcResult = this.mockMvc.perform(request).andDo(print()).andExpect(status().isOk()).andReturn();

        ProcessInstance processInstance = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProcessInstance.class);
        Assert.assertEquals(processInstance.getStatus(), ProcessInstance.StatusEnum.COMPLETED);
        Assert.assertEquals(processInstance.getProcessInstanceID(), StartControllerTest.processInstanceIdOrder2);
    }

    @Test
    public void test3_finishCollaboration() throws Exception{
        MockHttpServletRequestBuilder request = post("/process-instance-groups/"+ StartControllerTest.sellerProcessInstanceGroupIdContainingOrder2+"/finish")
                .header("Authorization", TestConfig.responderPersonId);
        MvcResult mvcResult = this.mockMvc.perform(request).andDo(print()).andExpect(status().isOk()).andReturn();
    }
}
