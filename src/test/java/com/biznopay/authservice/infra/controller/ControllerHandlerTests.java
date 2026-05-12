package com.biznopay.authservice.infra.controller;

import com.biznopay.authservice.config.PostgresContainerBase;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.exception.UnexpectedException;
import com.biznopay.authservice.infra.dto.RegisterSARequest;
import com.biznopay.authservice.mocks.Mocks;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSA;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@Tag("integration")
@ActiveProfiles("test")
@WebMvcTest(SAController.class)
public class ControllerHandlerTests extends PostgresContainerBase {
    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private MockMvc mvc;

    @MockitoBean
    private RegisterSA registerSA;

    @AfterAll
    static void tearDown() {
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    @DisplayName("Should return 400 and return RequiredFieldException when first name is empty on RegisterSA")
    public void shouldReturn400AndReturnRequiredFieldExceptionWhenFirstNameIsEmptyOnRegisterSA() throws Exception {
        Mockito.when(registerSA.execute(ArgumentMatchers.any())).thenThrow(new RequiredFieldException("First name", User.class.getName(), "USER-002"));
        RegisterSARequest registerSARequest = Mocks.registerSARequestMock();
        String request = new ObjectMapper().writeValueAsString(registerSARequest);
        mvc.perform(MockMvcRequestBuilders
                        .post("/supper-admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.message").value("First name is required"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.code").value("USER-002"));
    }

    @Test
    @DisplayName("Should return 500 and UnexpectedException when any unexpected error occurs")
    public void shouldReturn500AndUnexpectedExceptionWhenAnyUnexpectedErrorOccurs() throws Exception {
        UnexpectedException exception = new UnexpectedException("UNEXPECTED_ERROR-001");
        Mockito.when(registerSA.execute(ArgumentMatchers.any())).thenThrow(exception);
        RegisterSARequest registerSARequest = Mocks.registerSARequestMock();
        String request = new ObjectMapper().writeValueAsString(registerSARequest);
        mvc.perform(MockMvcRequestBuilders
                        .post("/supper-admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.message").value("Unexpected error! Please try again later."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.code").value("UNEXPECTED_ERROR-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.metadata").doesNotExist());
        Assertions.assertNull(exception.getMetadata());
    }
}
