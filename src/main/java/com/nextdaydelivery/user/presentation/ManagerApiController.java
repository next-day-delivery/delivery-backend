package com.nextdaydelivery.user.presentation;

import com.nextdaydelivery.global.dto.CommonResponse;
import com.nextdaydelivery.global.security.annotation.RequireMasterRole;
import com.nextdaydelivery.user.application.ManagerService;
import com.nextdaydelivery.user.presentation.dto.request.ManagerCreateRequest;
import com.nextdaydelivery.user.presentation.dto.response.ManagerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public ResponseEntity<CommonResponse<Page<ManagerResponse>>> getManagers(Pageable pageable) {
        Page<ManagerResponse> response = managerService.getManagers(pageable);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }
}
