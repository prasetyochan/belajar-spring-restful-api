package chandraprasetyo.restful.controller;

import chandraprasetyo.restful.entity.Address;
import chandraprasetyo.restful.entity.Contact;
import chandraprasetyo.restful.entity.User;
import chandraprasetyo.restful.model.*;
import chandraprasetyo.restful.repository.AddressRepository;
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
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
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


        Contact contact = new Contact();
        contact.setId("test");
        contact.setUser(user);
        contact.setFirstName("chandra");
        contact.setLastName("prasetyo");
        contact.setEmail("chandra@example.com");
        contact.setPhone("0811111");
        contactRepository.save(contact);
    }

    @Test
    void createAddressBadRequest() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
                post("/api/contacts/test/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info(response.getErrors());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createAddressSuccess() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("Jl. Street");
        request.setCity("East Jakarta");
        request.setProvince("DKI Jakarta");
        request.setPostalCode("13520");
        request.setCountry("Indonesia");

        mockMvc.perform(
                post("/api/contacts/test/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info(response.getErrors());
            assertNull(response.getErrors());
            assertEquals(request.getStreet(), response.getData().getStreet());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getProvince(), response.getData().getProvince());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());

            assertTrue(addressRepository.existsById(response.getData().getId()));

            System.out.println(response.getData());
        });
    }

    @Test
    void getAddressNotFound() throws Exception {

        mockMvc.perform(
                get("/api/contacts/test/addresses/testid")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            log.info(response.getErrors());
            System.out.println(response.getData());
        });
    }

    @Test
    void getAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet("Jl. Street");
        address.setCity("East Jakarta");
        address.setProvince("DKI Jakarta");
        address.setPostalCode("13520");
        address.setCountry("Indonesia");
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(address.getId(), response.getData().getId());
            assertEquals(address.getStreet(), response.getData().getStreet());
            assertEquals(address.getCity(), response.getData().getCity());
            assertEquals(address.getProvince(), response.getData().getProvince());
            assertEquals(address.getCountry(), response.getData().getCountry());
            assertEquals(address.getPostalCode(), response.getData().getPostalCode());

            System.out.println("-----RESPONSE-----");
            System.out.println(response);
        });
    }

    @Test
    void updateAddressBadRequest() throws Exception {
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
                put("/api/contacts/test/addresses/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info(response.getErrors());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet("Jl. Lama");
        address.setCity("Lama");
        address.setProvince("Lama");
        address.setPostalCode("13520");
        address.setCountry("Lama");
        addressRepository.save(address);


        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setStreet("Jl. Street Updated");
        request.setCity("East Jakarta Updated");
        request.setProvince("DKI Jakarta Updated");
        request.setPostalCode("13520");
        request.setCountry("Indonesia Updated");

        mockMvc.perform(
                put("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info(response.getErrors());
            assertNull(response.getErrors());
            assertEquals(request.getStreet(), response.getData().getStreet());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getProvince(), response.getData().getProvince());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());

            assertTrue(addressRepository.existsById(response.getData().getId()));

            System.out.println(response);
        });
    }

    @Test
    void deleteAddressNotFound() throws Exception {

        mockMvc.perform(
                get("/api/contacts/test/addresses/testid")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            log.info(response.getErrors());
            System.out.println(response.getData());
        });
    }

    @Test
    void deleteAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet("Jl. Lama");
        address.setCity("Lama");
        address.setProvince("Lama");
        address.setPostalCode("13520");
        address.setCountry("Lama");
        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals("OK", response.getData());
            assertFalse(addressRepository.existsById(address.getId()));
            System.out.println(response);
        });
    }

    @Test
    void listAddressNotFound() throws Exception {

        mockMvc.perform(
                get("/api/contacts/wrong/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            log.info(response.getErrors());
            System.out.println(response.getData());
        });
    }

    @Test
    void listAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("test").orElseThrow();

        for (int i = 0; i < 5; i++) {
            Address address = new Address();
            address.setId(UUID.randomUUID().toString());
            address.setContact(contact);
            address.setStreet("Jl. Street");
            address.setCity("East Jakarta");
            address.setProvince("DKI Jakarta");
            address.setPostalCode("13520");
            address.setCountry("Indonesia");
            addressRepository.save(address);
        }

        mockMvc.perform(
                get("/api/contacts/" + contact.getId() + "/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(5, response.getData().size());

            System.out.println("-----RESPONSE-----");
            for (int i = 0; i < 5; i++) {
                System.out.println(response.getData().get(i).getId());
            }

        });
    }


}