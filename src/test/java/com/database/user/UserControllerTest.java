package com.database.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        jdbcTemplate.update("DELETE FROM users WHERE name = ?", "TestAdmin");
        jdbcTemplate.update("DELETE FROM users WHERE email = ?", "testuser@example.com");
        
        // 使用 PasswordEncoder 生成正确的密码哈希
        String encodedPassword = passwordEncoder.encode("123456");
        
        // 插入登录测试用户
        jdbcTemplate.update(
                "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)",
                "TestAdmin", "testadmin@example.com", encodedPassword
        );
        // 获取插入用户的 ID
        testUserId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE name = ?", Long.class, "TestAdmin"
        );
    }

    @Test
    void createUser_success() throws Exception {
        var body = Map.of(
                "name", "TestUser",
                "email", "testuser@example.com",
                "password", "password123"
        );

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("TestUser"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }
    @Test
    void createUser_validationFail() throws Exception {
        var body = Map.of(
                "name", "",
                "email", "not-an-email",
                "password", "password123"
        );

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_passwordMissing_validationFail() throws Exception {
        var body = Map.of(
                "name", "TestUser",
                "email", "testuser@example.com"
        );

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_passwordHashed_andNotReturned() throws Exception {
        String rawPassword = "password123";
        var body = Map.of(
                "name", "TestUser",
                "email", "testuser@example.com",
                "password", rawPassword
        );

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password_hash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());

        String storedHash = jdbcTemplate.queryForObject(
                "SELECT password_hash FROM users WHERE email = ?",
                String.class,
                "testuser@example.com"
        );

        org.junit.jupiter.api.Assertions.assertNotNull(storedHash);
        org.junit.jupiter.api.Assertions.assertNotEquals(rawPassword, storedHash);
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches(rawPassword, storedHash));
    }

    String loginAndGetToken() throws Exception {
        var body = Map.of("name", "TestAdmin", "password", "123456");

        String resp = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(resp).path("data").asText();
    }

    @Test
    void getUser_withJwt_success() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(
                        get("/users/" + testUserId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.intValue()))
                .andExpect(jsonPath("$.name").value("TestAdmin"));
    }

    @Test
    void getUser_notFound_returnsErrorEnvelope() throws Exception {
        String token = loginAndGetToken();
        Long maxId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM users", Long.class);
        long missingId = (maxId == null ? 1L : maxId + 1000);

        mockMvc.perform(
                        get("/users/" + missingId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void getUser_withoutJwt_unauthorized() throws Exception {
        mockMvc.perform(get("/users/" + testUserId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_withJwt_success() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(
                        delete("/users/" + testUserId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = ?",
                Integer.class,
                testUserId
        );
        org.junit.jupiter.api.Assertions.assertEquals(0, count);
    }


}
