package com.example.carins;

import com.example.carins.service.CarService;
import com.example.carins.web.CarController;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarController.class)
public class CarControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CarService carService;

    @Test
    @DisplayName("400 when date format is invalid")
    void invalidDateFormatReturnsBadRequest() throws Exception {
        mvc.perform(get("/api/cars/{carId}/insurance-valid", 1)
                .param("date", "13-09-2025"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Date format must be YYYY-MM-DD"));
    }

    @Test
    @DisplayName("400 when date is out of range")
    void outOfRangeDateReturnsBadRequest() throws Exception {
        mvc.perform(get("/api/cars/{carId}/insurance-valid", 1)
                .param("date", "1800-01-01"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Date must be between 1900-01-01 and 2100-12-31"));

        mvc.perform(get("/api/cars/{carId}/insurance-valid", 1)
                .param("date", "2200-01-01"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Date must be between 1900-01-01 and 2100-12-31"));
    }

    @Test
    @DisplayName("404 when car don't exists")
    void notExistingCarReturnsNotFound() throws Exception {
        mvc.perform(get("/api/cars/{carId}/insurance-valid", 99)
                .param("date", "2025-01-01"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("200 with JSON response on valid inputs")
    void okWithValidInputs() throws Exception {
        given(carService.carExists(1L)).willReturn(true);
        given(carService.isInsuranceValid(eq(1L), any())).willReturn(true);

        mvc.perform(get("/api/cars/{carId}/insurance-valid", 1)
                .param("date", "2025-01-01")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.carId").value(1))
            .andExpect(jsonPath("$.date").value("2025-01-01"))
            .andExpect(jsonPath("$.valid").value(true));
    }
}
