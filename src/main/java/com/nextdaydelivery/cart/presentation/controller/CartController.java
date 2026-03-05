package com.nextdaydelivery.cart.presentation.controller;

import com.nextdaydelivery.cart.application.service.CartService;
import com.nextdaydelivery.cart.presentation.dto.request.ReqPatchCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.request.ReqPostCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResGetCartItemsDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResPatchCartItemDto;
import com.nextdaydelivery.cart.presentation.dto.response.ResPostCartItemDto;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ResPostCartItemDto> addCartItem(
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody ReqPostCartItemDto request
    ) {
        return ResponseEntity.ok(cartService.addCartItem(userId, request));
    }

    @GetMapping
    public ResponseEntity<ResGetCartItemsDto> getActiveCartItems(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getActiveCartItems(userId));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ResPatchCartItemDto> updateCartItem(
        @RequestHeader("X-User-Id") Long userId,
        @PathVariable UUID productId,
        @Valid @RequestBody ReqPatchCartItemDto request
    ) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteCartItem(
        @RequestHeader("X-User-Id") Long userId,
        @PathVariable UUID productId
    ) {
        cartService.deleteCartItem(userId, productId);
        return ResponseEntity.noContent().build(); // 응답할 data가 없음.
    }
}
