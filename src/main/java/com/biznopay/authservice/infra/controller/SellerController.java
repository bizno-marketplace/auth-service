package com.biznopay.authservice.infra.controller;

import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.dto.RegisterSellerRequest;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSeller;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSellerInput;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSellerOutput;
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
@Tag(name = "Sellers")
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
    private final RegisterSeller registerSeller;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody @Valid RegisterSellerRequest request) {
        RegisterSellerInput input = UserMapper.toRegisterSellerInput(request);
        RegisterSellerOutput output = registerSeller.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }
}
