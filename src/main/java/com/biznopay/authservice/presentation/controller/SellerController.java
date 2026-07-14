package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.presentation.dto.RegisterSellerRequest;
import com.biznopay.authservice.presentation.dto.RejectSellerRequest;
import com.biznopay.authservice.presentation.dto.ResubmitSellerRequest;
import com.biznopay.authservice.presentation.dto.UpdateSellerRequest;
import com.biznopay.authservice.presentation.validator.BiDocumentValidator;
import com.biznopay.authservice.usecase.seller.approveSeller.ApproveSeller;
import com.biznopay.authservice.usecase.seller.approveSeller.ApproveSellerInput;
import com.biznopay.authservice.usecase.seller.register.RegisterSeller;
import com.biznopay.authservice.usecase.seller.register.RegisterSellerInput;
import com.biznopay.authservice.usecase.seller.register.RegisterSellerOutput;
import com.biznopay.authservice.usecase.seller.rejectSeller.RejectSeller;
import com.biznopay.authservice.usecase.seller.rejectSeller.RejectSellerInput;
import com.biznopay.authservice.usecase.seller.resubmitseller.ResubmitSeller;
import com.biznopay.authservice.usecase.seller.resubmitseller.ResubmitSellerInput;
import com.biznopay.authservice.usecase.seller.resubmitseller.ResubmitSellerOutput;
import com.biznopay.authservice.usecase.seller.updateSeller.UpdateSeller;
import com.biznopay.authservice.usecase.seller.updateSeller.UpdateSellerInput;
import com.biznopay.authservice.usecase.seller.updateSeller.UpdateSellerOutput;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Tag(name = "Sellers")
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
    private final RegisterSeller registerSeller;
    private final ApproveSeller approveSeller;
    private final RejectSeller rejectSeller;
    private final ResubmitSeller resubmitSeller;
    private final UpdateSeller updateSeller;

    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> register(
            @RequestPart("data") RegisterSellerRequest request,
            @RequestPart(value = "biFrontPhoto", required = false) MultipartFile biFrontPhoto,
            @RequestPart(value = "biBackPhoto", required = false) MultipartFile biBackPhoto
    ) throws IOException {
        BiDocumentValidator.validate(biFrontPhoto, biBackPhoto);
        RegisterSellerInput input = UserMapper.toRegisterSellerInput(request, biFrontPhoto, biBackPhoto);
        RegisterSellerOutput output = registerSeller.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Object>> approve(@PathVariable("id") String sellerId) {
        ApproveSellerInput input = new ApproveSellerInput(sellerId);
        approveSeller.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, null, null));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Object>> reject(@PathVariable("id") String sellerId, @Valid @RequestBody RejectSellerRequest request) {
        RejectSellerInput input = UserMapper.toRejectSellerInput(sellerId, request);
        rejectSeller.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, null, null));
    }

    @PatchMapping("/resubmit")
    public ResponseEntity<ApiResponse<Object>> resubmit(
            @RequestPart("data") ResubmitSellerRequest request,
            @RequestPart(value = "biFrontPhoto", required = false) MultipartFile biFrontPhoto,
            @RequestPart(value = "biBackPhoto", required = false) MultipartFile biBackPhoto
    ) throws IOException {
        BiDocumentValidator.validateForResubmit(biFrontPhoto, biBackPhoto);
        ResubmitSellerInput input = UserMapper.toResubmitSellerInput(request, biFrontPhoto, biBackPhoto);
        ResubmitSellerOutput output = resubmitSeller.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<Object>> update(@Valid @RequestBody UpdateSellerRequest request) {
        UpdateSellerInput input = UserMapper.toUpdateSellerInput(request);
        UpdateSellerOutput output = updateSeller.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }
}
