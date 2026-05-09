package com.biznopay.authservice.infra.controller;

import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.dto.RegisterSARequest;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSA;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSAInput;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSAOutput;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Super Admins")
@RequiredArgsConstructor
@RequestMapping("/supper-admins")
public class SAController {
    private final RegisterSA registerSA;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody @Valid RegisterSARequest request) {
        RegisterSAInput input = UserMapper.toRegisterSAInput(request);
        RegisterSAOutput output = registerSA.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(FuncUtils.buildResponseBody(true, output, null));
    }
}
