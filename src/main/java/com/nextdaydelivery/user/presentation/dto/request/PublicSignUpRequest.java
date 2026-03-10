package com.nextdaydelivery.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nextdaydelivery.user.domain.entity.enums.UserRole;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "role",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CustomerSignUpRequest.class, name = "CUSTOMER"),
        @JsonSubTypes.Type(value = OwnerSignUpRequest.class, name = "OWNER")
})
public sealed interface PublicSignUpRequest permits CustomerSignUpRequest, OwnerSignUpRequest {
    String username();

    String nickname();

    String email();

    String password();

    UserRole role();
}
