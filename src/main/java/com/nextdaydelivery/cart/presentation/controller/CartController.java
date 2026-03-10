package com.nextdaydelivery.cart.presentation.controller;

import com.nextdaydelivery.cart.application.service.CartService;
import com.nextdaydelivery.cart.presentation.dto.request.ReqPatchCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.request.ReqPostCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResGetCartItemsDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResPatchCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResPostCartItemDto;
import com.nextdaydelivery.global.security.annotation.AuthUser;
import com.nextdaydelivery.global.security.annotation.RequireCustomerRole;
import com.nextdaydelivery.global.security.dto.AuthUserDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        @AuthUser AuthUserDto authUser,
        @Valid @RequestBody ReqPostCartItemDto request
    ) {
        return ResponseEntity.ok(cartService.addCartItem(authUser.userId(), request));
    }

    @GetMapping
    public ResponseEntity<ResGetCartItemsDto> getActiveCartItems(@AuthUser AuthUserDto authUser) {
        return ResponseEntity.ok(cartService.getActiveCartItems(authUser.userId()));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ResPatchCartItemDto> updateCartItem(
        @AuthUser AuthUserDto authUser,
        @PathVariable UUID productId,
        @Valid @RequestBody ReqPatchCartItemDto request
    ) {
        return ResponseEntity.ok(cartService.updateCartItem(authUser.userId(), productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteCartItem(
        @AuthUser AuthUserDto authUser,
        @PathVariable UUID productId
    ) {
        cartService.deleteCartItem(authUser.userId(), productId);
        return ResponseEntity.noContent().build(); // 응답할 data가 없음.
    }
}
