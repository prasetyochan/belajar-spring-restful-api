package chandraprasetyo.restful.controller;

import chandraprasetyo.restful.entity.Contact;
import chandraprasetyo.restful.entity.User;
import chandraprasetyo.restful.model.ContactResponse;
import chandraprasetyo.restful.model.CreateContactRequest;
import chandraprasetyo.restful.model.UpdateContactRequest;
import chandraprasetyo.restful.model.WebResponse;
import chandraprasetyo.restful.repository.ContactRepository;
import chandraprasetyo.restful.repository.UserRepository;
import chandraprasetyo.restful.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        userRepository.deleteAll();

        //SetUp user, user must have logged-in first
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
        userRepository.save(user);
    }

    @Test
    void createContactBadRequest() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setEmail("salah");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            assertNotNull(response.getErrors());
            log.info(response.getErrors());
        });
    }

    @Test
    void createContactSuccess() throws Exception{
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("Chandra");
        request.setLastName("Prasetyo");
        request.setEmail("chandra@example.com");
        request.setPhone("081911111");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info("getError: " + response.getErrors());
            assertNull(response.getErrors());
            assertEquals("Chandra", response.getData().getFirstName());
            assertEquals("Prasetyo", response.getData().getLastName());
            assertEquals("chandra@example.com", response.getData().getEmail());
            assertEquals("081911111", response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));

        });
    }

    @Test
    void getContactNotFound() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setEmail("salah");

        mockMvc.perform(
                get("/api/contacts/22331")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            log.info(response.getErrors());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getContactSuccess() throws Exception{
            User user = userRepository.findById("test").orElseThrow();

            Contact contact = new Contact();
            contact.setId(UUID.randomUUID().toString());
            contact.setUser(user);
            contact.setFirstName("chandra");
            contact.setLastName("prasetyo");
            contact.setEmail("chandra@example.com");
            contact.setPhone("0811111");
            contactRepository.save(contact);

            mockMvc.perform(
                    get("/api/contacts/" + contact.getId())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-TOKEN", "test")
            ).andExpectAll(
                    status().isOk()
            ).andDo(result -> {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
                log.info("error?" + response.getErrors());
                assertNull(response.getErrors());

                assertEquals(contact.getId(), response.getData().getId());
                assertEquals(contact.getFirstName(), response.getData().getFirstName());
                assertEquals(contact.getLastName(), response.getData().getLastName());
                assertEquals(contact.getEmail(), response.getData().getEmail());
                assertEquals(contact.getPhone(), response.getData().getPhone());

                System.out.println(response.getData());

            });
        }

    @Test
    void updateContactBadRequest() throws Exception {
        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("");
        request.setEmail("salah");

        mockMvc.perform(
                put("/api/contacts/1234")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            assertNotNull(response.getErrors());
            log.info(response.getErrors());
        });
    }

    @Test
    void updateContactSuccess() throws Exception{
        //First, create Contact
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("chandra");
        contact.setLastName("prasetyo");
        contact.setEmail("chandra@example.com");
        contact.setPhone("0811111");
        contactRepository.save(contact);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Budi");
        request.setLastName("Nugraha");
        request.setEmail("budi@example.com");
        request.setPhone("0819");

        mockMvc.perform(
                put("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info("getError: " + response.getErrors());
            assertNull(response.getErrors());
            assertEquals(request.getFirstName(), response.getData().getFirstName());
            assertEquals(request.getLastName(), response.getData().getLastName());
            assertEquals(request.getEmail(), response.getData().getEmail());
            assertEquals(request.getPhone(), response.getData().getPhone());
            assertTrue(contactRepository.existsById(response.getData().getId()));

            System.out.println(request);
            System.out.println(response.getData());

        });
    }

    @Test
    void deleteContactNotFound() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setEmail("salah");

        mockMvc.perform(
                delete("/api/contacts/22331")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            log.info(response.getErrors());
            assertNotNull(response.getErrors());
            System.out.println(response);
        });
    }

    @Test
    void deleteContactSuccess() throws Exception{
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("chandra");
        contact.setLastName("prasetyo");
        contact.setEmail("chandra@example.com");
        contact.setPhone("0811111");
        contactRepository.save(contact);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info("error?" + response.getErrors());
            assertNull(response.getErrors());
            assertEquals("OK", response.getData());

            System.out.println(response);
            System.out.println(response.getData());

        });
    }

    @Test
    void searchNotFound() throws Exception{

        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info("error?" + response.getErrors());
            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
            assertEquals(0, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());

            System.out.println(response);
            System.out.println(response.getData());

        });
    }

    @Test
    void searchSuccess() throws Exception{
        User user = userRepository.findById("test").orElseThrow();

        for (int i = 0; i < 100; i++) {
            Contact contact = new Contact();
            contact.setId(UUID.randomUUID().toString());
            contact.setUser(user);
            contact.setFirstName("Chandra " + i);
            contact.setLastName("Prasetyo ");
            contact.setEmail("chandra" + i +"@example.com");
            contact.setPhone("0811111");
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("name", "Chandra")
                        .queryParam("page", "0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info("error?" + response.getErrors());
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());

            System.out.println("-----------------RESPONSE-----------------");
            System.out.println(response);
            System.out.println("-----------------RESPONSE DATA-----------------");
            System.out.println(response.getData());
            System.out.println("-----------------RESPONSE PAGING-----------------");
            System.out.println(response.getPaging());

        });

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("email", "example.com")
                        .queryParam("page", "0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info("error?" + response.getErrors());
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());

            System.out.println("-----------------RESPONSE-----------------");
            System.out.println(response);
            System.out.println("-----------------RESPONSE DATA-----------------");
            System.out.println(response.getData());
            System.out.println("-----------------RESPONSE PAGING-----------------");
            System.out.println(response.getPaging());

        });

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("phone", "81")
                        .queryParam("page", "0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info("error?" + response.getErrors());
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());

            System.out.println("-----------------RESPONSE-----------------");
            System.out.println(response);
            System.out.println("-----------------RESPONSE DATA-----------------");
            System.out.println(response.getData());
            System.out.println("-----------------RESPONSE PAGING-----------------");
            System.out.println(response.getPaging());

        });

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("phone", "81")
                        .queryParam("page", "1000")
                        .queryParam("page", "0")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info("error?" + response.getErrors());
            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(1000, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());

            System.out.println("-----------------RESPONSE-----------------");
            System.out.println(response);
            System.out.println("-----------------RESPONSE DATA-----------------");
            System.out.println(response.getData());
            System.out.println("-----------------RESPONSE PAGING-----------------");
            System.out.println(response.getPaging());

        });

    }

    @Test
    void searchUsingNameShowAll() throws Exception{
        User user = userRepository.findById("test").orElseThrow();

        //Insert 100
        for (int i = 0; i < 100; i++) {
            Contact contact = new Contact();
            contact.setId(UUID.randomUUID().toString());
            contact.setUser(user);
            contact.setFirstName("Chandra " + i);
            contact.setLastName("Prasetyo");
            contact.setEmail("chandra" + i +"@example.com");
            contact.setPhone("0811111");
            contactRepository.save(contact);
        }

        //Select and Print 100/All
        for (int i = 0; i < 10; i++) {
            String x = String.valueOf(i);
            final int current = i;
            mockMvc.perform(
                    get("/api/contacts")
                            .queryParam("name", "Chandra")
                            .queryParam("page", x)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-TOKEN", "test")
            ).andExpectAll(
                    status().isOk()
            ).andDo(result -> {
                WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
                log.info("error?" + response.getErrors());
                assertNull(response.getErrors());
                assertEquals(10, response.getData().size());
                assertEquals(10, response.getPaging().getTotalPage());
                assertEquals(current, response.getPaging().getCurrentPage());
                assertEquals(10, response.getPaging().getSize());

                System.out.println("-----------------RESPONSE-----------------");
                System.out.println(response);
            });
        }
    }



}