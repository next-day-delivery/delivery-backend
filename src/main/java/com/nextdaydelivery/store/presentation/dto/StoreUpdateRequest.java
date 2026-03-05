package com.nextdaydelivery.store.presentation.dto;

import java.util.List;
import java.util.UUID;

public record StoreUpdateRequest(
        String name,
        String sido,
        String sigungu,
        String dong,
        String detailAddress,
        List<UUID> categoryIds
) {}