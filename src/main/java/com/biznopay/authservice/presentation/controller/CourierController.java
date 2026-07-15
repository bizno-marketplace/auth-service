package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.presentation.dto.RegisterCourierRequest;
import com.biznopay.authservice.usecase.courier.register.RegisterCourier;
import com.biznopay.authservice.usecase.courier.register.RegisterCourierInput;
import com.biznopay.authservice.usecase.courier.register.RegisterCourierOutput;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Courier")
@RequiredArgsConstructor
@RequestMapping("/couriers")
public class CourierController {
    private final RegisterCourier registerCourier;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody @Valid RegisterCourierRequest request) {
        RegisterCourierInput input = UserMapper.toRegisterCourierInout(request);
        RegisterCourierOutput output = registerCourier.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }
}
