package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.presentation.dto.RegisterBuyerRequest;
import com.biznopay.authservice.usecase.user.register.buyer.RegisterBuyer;
import com.biznopay.authservice.usecase.user.register.buyer.RegisterBuyerInput;
import com.biznopay.authservice.usecase.user.register.buyer.RegisterBuyerOutput;
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
@Tag(name = "Buyers")
@RequiredArgsConstructor
@RequestMapping("/buyers")
public class BuyerController {
    private final RegisterBuyer registerBuyer;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody @Valid RegisterBuyerRequest request) {
        RegisterBuyerInput input = UserMapper.toRegisterBuyerInput(request);
        RegisterBuyerOutput output = registerBuyer.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }
}
