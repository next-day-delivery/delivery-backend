package com.nextdaydelivery.delivery.presentation.controller;

import com.nextdaydelivery.delivery.application.service.DeliveryService;
import com.nextdaydelivery.delivery.presentation.dto.request.DeliveryStatusRequest;
import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.annotation.RequireManagerRole;
import com.nextdaydelivery.global.security.annotation.RequireOwnerRole;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @RequireOwnerRole
    @PatchMapping("/{deliveryId}")
    public CommonResponse<Void> updateDeliveryStatusByOwner(
            @PathVariable UUID deliveryId,
            @RequestBody DeliveryStatusRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails

    ) {
        deliveryService.updateDeliveryStatusByOwner(deliveryId, request.status(),
                principalDetails.getAuthUserDto().userId());
        return CommonResponse.onSuccess();
    }


    @RequireManagerRole
    @PatchMapping("/{deliveryId}/manager")
    public CommonResponse<Void> updateDeliveryStatusByManager(
            @PathVariable UUID deliveryId,
            @RequestBody DeliveryStatusRequest request
    ) {
        deliveryService.updateDeliveryStatusByManager(deliveryId, request.status());
        return CommonResponse.onSuccess();
    }

}
