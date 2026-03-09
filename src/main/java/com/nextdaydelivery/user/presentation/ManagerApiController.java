package com.nextdaydelivery.user.presentation;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.annotation.RequireMasterRole;
import com.nextdaydelivery.user.application.ManagerService;
import com.nextdaydelivery.user.presentation.dto.request.ManagerCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
@RequireMasterRole
public class ManagerApiController {
    private final ManagerService managerService;

    @PostMapping
    public CommonResponse<Long> createManager(@Valid @RequestBody ManagerCreateRequest request) {
        Long managerId = managerService.createManager(request);
        return CommonResponse.onSuccess(managerId);
    }
}
