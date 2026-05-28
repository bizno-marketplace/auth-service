package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.presentation.dto.RegisterSellerRequest;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSeller;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSellerInput;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSellerOutput;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Tag(name = "Sellers")
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
    private final RegisterSeller registerSeller;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestPart("data") RegisterSellerRequest request,
            @RequestPart("biFrontPhoto") MultipartFile biFrontPhoto,
            @RequestPart("biBackPhoto") MultipartFile biBackPhoto
    ) throws IOException {
        RegisterSellerInput input = UserMapper.toRegisterSellerInput(request, biFrontPhoto, biBackPhoto);
        RegisterSellerOutput output = registerSeller.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }
}
