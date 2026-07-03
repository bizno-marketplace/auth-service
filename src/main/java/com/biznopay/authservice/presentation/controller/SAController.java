package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.presentation.dto.RegisterSARequest;
import com.biznopay.authservice.usecase.sa.RegisterSA;
import com.biznopay.authservice.usecase.sa.RegisterSAInput;
import com.biznopay.authservice.usecase.sa.RegisterSAOutput;
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
@Tag(name = "Super Admins")
@RequiredArgsConstructor
@RequestMapping("/supper-admins")
public class SAController {
    private final RegisterSA registerSA;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody @Valid RegisterSARequest request) {
        RegisterSAInput input = UserMapper.toRegisterSAInput(request);
        RegisterSAOutput output = registerSA.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }
}
