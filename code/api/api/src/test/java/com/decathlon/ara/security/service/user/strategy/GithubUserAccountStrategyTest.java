package com.decathlon.ara.security.service.user.strategy;

import com.decathlon.ara.domain.security.member.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubUserAccountStrategyTest {

    @InjectMocks
    private GithubUserAccountStrategy githubUserAccountStrategy;

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void getUserAccount_returnGithubUserAccountHavingNeitherFirstNameNorLastName_whenFullNameIsBlank(String fullName) {
        // Given
        var githubUser = mock(OAuth2User.class);
        var userLogin = "user-login";
        var userEmail = "user-email";
        var userPictureUrl = "user-picture-url";

        var userEntityLogin = "user-entity-login";
        var providerName = "provider-name";
        var userEntity = new UserEntity(userEntityLogin, providerName);

        // When
        when(githubUser.getAttribute(StandardClaimNames.NAME)).thenReturn(fullName);
        when(githubUser.getAttribute(GithubUserAccountStrategy.LOGIN_FIELD)).thenReturn(userLogin);
        when(githubUser.getAttribute(StandardClaimNames.EMAIL)).thenReturn(userEmail);
        when(githubUser.getAttribute(GithubUserAccountStrategy.PICTURE_URL_FIELD)).thenReturn(userPictureUrl);

        // Then
        assertThat(githubUserAccountStrategy.getFirstName(githubUser)).isNotPresent();
        assertThat(githubUserAccountStrategy.getLastName(githubUser)).isNotPresent();
        assertThat(githubUserAccountStrategy.getLogin(githubUser)).isEqualTo(userLogin);
        assertThat(githubUserAccountStrategy.getEmail(githubUser)).contains(userEmail);
        assertThat(githubUserAccountStrategy.getPictureUrl(githubUser)).contains(userPictureUrl);

        var userAccount = githubUserAccountStrategy.getUserAccount(githubUser, userEntity);
        assertThat(userAccount)
                .extracting(
                        "login",
                        "firstName",
                        "lastName",
                        "email",
                        "pictureUrl"
                )
                .contains(
                        userEntityLogin,
                        null,
                        null,
                        userEmail,
                        userPictureUrl
                );
    }

    @Test
    void getUserAccount_returnGithubUserAccountHavingAFirstNameButNoLastName_whenFullNameHasOnlyOnePart() {
        // Given
        var githubUser = mock(OAuth2User.class);
        var userLogin = "user-login";
        var fullName = "  user-first-name  ";
        var userEmail = "user-email";
        var userPictureUrl = "user-picture-url";

        var userEntityLogin = "user-entity-login";
        var providerName = "provider-name";
        var userEntity = new UserEntity(userEntityLogin, providerName);

        // When
        when(githubUser.getAttribute(StandardClaimNames.NAME)).thenReturn(fullName);
        when(githubUser.getAttribute(GithubUserAccountStrategy.LOGIN_FIELD)).thenReturn(userLogin);
        when(githubUser.getAttribute(StandardClaimNames.EMAIL)).thenReturn(userEmail);
        when(githubUser.getAttribute(GithubUserAccountStrategy.PICTURE_URL_FIELD)).thenReturn(userPictureUrl);

        // Then
        var expectedFirstName = "user-first-name";

        assertThat(githubUserAccountStrategy.getFirstName(githubUser)).contains(expectedFirstName);
        assertThat(githubUserAccountStrategy.getLastName(githubUser)).isNotPresent();
        assertThat(githubUserAccountStrategy.getLogin(githubUser)).isEqualTo(userLogin);
        assertThat(githubUserAccountStrategy.getEmail(githubUser)).contains(userEmail);
        assertThat(githubUserAccountStrategy.getPictureUrl(githubUser)).contains(userPictureUrl);

        var userAccount = githubUserAccountStrategy.getUserAccount(githubUser, userEntity);
        assertThat(userAccount)
                .extracting(
                        "login",
                        "firstName",
                        "lastName",
                        "email",
                        "pictureUrl"
                )
                .contains(
                        userEntityLogin,
                        expectedFirstName,
                        null,
                        userEmail,
                        userPictureUrl
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {"        user-first-name     user-last-name     ", "     user-first-name                   and-some middle names here to ignore                user-last-name    "})
    void getUserAccount_returnGithubUserAccountHavingAFirstNameAndALastName_whenFullNameHasTwoPartsOrMore(String fullName) {
        // Given
        var githubUser = mock(OAuth2User.class);
        var userLogin = "user-login";
        var userEmail = "user-email";
        var userPictureUrl = "user-picture-url";

        var userEntityLogin = "user-entity-login";
        var providerName = "provider-name";
        var userEntity = new UserEntity(userEntityLogin, providerName);

        // When
        when(githubUser.getAttribute(StandardClaimNames.NAME)).thenReturn(fullName);
        when(githubUser.getAttribute(GithubUserAccountStrategy.LOGIN_FIELD)).thenReturn(userLogin);
        when(githubUser.getAttribute(StandardClaimNames.EMAIL)).thenReturn(userEmail);
        when(githubUser.getAttribute(GithubUserAccountStrategy.PICTURE_URL_FIELD)).thenReturn(userPictureUrl);

        // Then
        var expectedFirstName = "user-first-name";
        var expectedLastName = "user-last-name";

        assertThat(githubUserAccountStrategy.getFirstName(githubUser)).contains(expectedFirstName);
        assertThat(githubUserAccountStrategy.getLastName(githubUser)).contains(expectedLastName);
        assertThat(githubUserAccountStrategy.getLogin(githubUser)).isEqualTo(userLogin);
        assertThat(githubUserAccountStrategy.getEmail(githubUser)).contains(userEmail);
        assertThat(githubUserAccountStrategy.getPictureUrl(githubUser)).contains(userPictureUrl);

        var userAccount = githubUserAccountStrategy.getUserAccount(githubUser, userEntity);
        assertThat(userAccount)
                .extracting(
                        "login",
                        "firstName",
                        "lastName",
                        "email",
                        "pictureUrl"
                )
                .contains(
                        userEntityLogin,
                        expectedFirstName,
                        expectedLastName,
                        userEmail,
                        userPictureUrl
                );
    }
}
