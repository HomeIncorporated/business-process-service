package eu.nimble.service.bp.impl.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.nimble.service.bp.model.trust.NegotiationRatings;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
public class Test26_TrustControllerTest2 {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final String partyID = "706";

    @Test
    public void test1_getRatingsSummary() throws Exception {
        MockHttpServletRequestBuilder request = get("/ratingsSummary")
                .header("Authorization", TestConfig.responderPersonId)
                .param("partyId",partyID);
        MvcResult mvcResult = this.mockMvc.perform(request).andDo(print()).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void test2_listAllIndividualRatingsAndReviews() throws Exception {
        MockHttpServletRequestBuilder request = get("/ratingsAndReviews")
                .header("Authorization", TestConfig.responderPersonId)
                .param("partyId",partyID);
        MvcResult mvcResult = this.mockMvc.perform(request).andDo(print()).andExpect(status().isOk()).andReturn();

        List<NegotiationRatings> negotiationRatings = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),new TypeReference<List<NegotiationRatings>>(){});
        // get ratings for the process instance id
        for(NegotiationRatings ratings:negotiationRatings){
            if(ratings.getProcessInstanceID().equals(Test25_TrustControllerTest.processInstanceId)){
                Assert.assertEquals(6,ratings.getRatings().size());
            }
        }
    }
}
