package com.decathlon.ara.domain.security.member.user.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserEntityRoleOnProjectTest {

    @Test
    void getScopeFromString_returnAdmin_whenStringIsAdmin() {
        // Given
        var scopeAsString = "ADMIN";

        // When

        // Then
        var scope = UserEntityRoleOnProject.ScopedUserRoleOnProject.getScopeFromString(scopeAsString);
        assertThat(scope)
                .isPresent()
                .contains(UserEntityRoleOnProject.ScopedUserRoleOnProject.ADMIN);
    }

    @Test
    void getScopeFromString_returnMaintainer_whenStringIsMaintainer() {
        // Given
        var scopeAsString = "MAINTAINER";

        // When

        // Then
        var scope = UserEntityRoleOnProject.ScopedUserRoleOnProject.getScopeFromString(scopeAsString);
        assertThat(scope)
                .isPresent()
                .contains(UserEntityRoleOnProject.ScopedUserRoleOnProject.MAINTAINER);
    }

    @Test
    void getScopeFromString_returnMember_whenStringIsMember() {
        // Given
        var scopeAsString = "MEMBER";

        // When

        // Then
        var scope = UserEntityRoleOnProject.ScopedUserRoleOnProject.getScopeFromString(scopeAsString);
        assertThat(scope)
                .isPresent()
                .contains(UserEntityRoleOnProject.ScopedUserRoleOnProject.MEMBER);
    }

    @Test
    void getScopeFromString_returnMember_whenStringIsMemberButWithDifferentCase() {
        // Given
        var scopeAsString = "mEmBeR";

        // When

        // Then
        var scope = UserEntityRoleOnProject.ScopedUserRoleOnProject.getScopeFromString(scopeAsString);
        assertThat(scope)
                .isPresent()
                .contains(UserEntityRoleOnProject.ScopedUserRoleOnProject.MEMBER);
    }

    @Test
    void getScopeFromString_returnEmptyOptional_whenStringIsNull() {
        // Given

        // When

        // Then
        var scope = UserEntityRoleOnProject.ScopedUserRoleOnProject.getScopeFromString(null);
        assertThat(scope).isNotPresent();
    }

    @Test
    void getScopeFromString_returnEmptyOptional_whenScopeIsUnknown() {
        // Given
        var scopeAsString = "unknown-scope";

        // When

        // Then
        var scope = UserEntityRoleOnProject.ScopedUserRoleOnProject.getScopeFromString(scopeAsString);
        assertThat(scope).isNotPresent();
    }
}
