package com.nextdaydelivery.cart.presentation.controller;

import com.nextdaydelivery.cart.application.service.CartService;
import com.nextdaydelivery.cart.presentation.dto.request.ReqPatchCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.request.ReqPostCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResGetCartItemsDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResPatchCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResPostCartItemDto;
import com.nextdaydelivery.global.security.annotation.RequireCustomerRole;
import com.nextdaydelivery.global.security.principal.PrincipalDetails;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@RequireCustomerRole
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ResPostCartItemDto> addCartItem(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @Valid @RequestBody ReqPostCartItemDto request
    ) {
        Long userId = principalDetails.getAuthUserDto().userId();
        return ResponseEntity.ok(cartService.addCartItem(userId, request));
    }

    @GetMapping
    public ResponseEntity<ResGetCartItemsDto> getActiveCartItems(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getAuthUserDto().userId();
        return ResponseEntity.ok(cartService.getActiveCartItems(userId));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ResPatchCartItemDto> updateCartItem(
        @AuthenticationPrincipal  PrincipalDetails principalDetails,
        @PathVariable UUID productId,
        @Valid @RequestBody ReqPatchCartItemDto request
    ) {
        Long userId = principalDetails.getAuthUserDto().userId();
        return ResponseEntity.ok(cartService.updateCartItem(userId, productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteCartItem(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable UUID productId
    ) {
        Long userId = principalDetails.getAuthUserDto().userId();
        cartService.deleteCartItem(userId, productId);
        return ResponseEntity.noContent().build(); // 응답할 data가 없음.
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCart(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getAuthUserDto().userId();
        cartService.deleteActiveCart(userId);
        return ResponseEntity.noContent().build();
    }
}
