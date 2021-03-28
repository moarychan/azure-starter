# Figure Out OAuth2AuthorizationRequestResolver

## Key classes

- `OAuth2AuthorizationRequestRedirectFilter`

  This Filter initiates the authorization code grant or implicit grant flow by redirecting the End-User's user-agent to the Authorization Server's Authorization Endpoint.

- `OAuth2LoginAuthenticationFilter`

  An implementation of an AbstractAuthenticationProcessingFilter for OAuth 2.0 Login. In the filter chain `VirtualFilterChain`, `OAuth2AuthorizationRequestRedirectFilter` comes before `OAuth2LoginAuthenticationFilter`.

- `DefaultOAuth2AuthorizationRequestResolver`

  An implementation of an OAuth2AuthorizationRequestResolver that attempts to resolve an OAuth2AuthorizationRequest from the provided HttpServletRequest using the default request URI pattern /oauth2/authorization/{registrationId}.

- `OAuth2AuthorizedClientArgumentResolver`

  An implementation of a HandlerMethodArgumentResolver that is capable of resolving a method parameter to an argument value of type OAuth2AuthorizedClient.

- `DefaultOAuth2AuthorizedClientManager`

  The default implementation of an OAuth2AuthorizedClientManager for use within the context of a HttpServletRequest.

- `DelegatingOAuth2AuthorizedClientProvider`

  An implementation of an OAuth2AuthorizedClientProvider that simply delegates to it's internal List of OAuth2AuthorizedClientProvider(s).

- `WebClient`

  Provides an easy mechanism for using an OAuth2AuthorizedClient to make OAuth 2.0 requests by including the access token as a bearer token.

## Where to call `OAuth2AuthorizationRequestResolver#resolve` method

### Triggered by end user specific URI request

  The default implementation is `DefaultOAuth2AuthorizationRequestResolver`, [here][end-user-uri-request-exec] is the code executed.
  Check every request through the filter chain, if the request matches the URI pattern '/oauth2/authorization/{registrationId}',
  then the resolver will return a authorization code request, the redirect filter will send the redirection to user agent. This is usually the client registration of the login process.

   ```java
   OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestResolver.resolve(request);
   if (authorizationRequest != null) {
       this.sendRedirectForAuthorization(request, response, authorizationRequest);
       return;
   }
   ```

### Triggered by OAuth2 Authorized Client annotation

This annotation `@RegisteredOAuth2AuthorizedClient` will trigger the OAuth2 authorization flow when the authorization gran type is `authorization_code`,
the configured client registration will occur `ClientAuthorizationRequiredException` exception, then `OAuth2AuthorizationRequestRedirectFilter` filter will
catch the exception, and try to resolve out the authorization request with special client registration id. This is usually a non-login client registration.

Let's find out the key methods:
1. Call a rest api with `@RegisteredOAuth2AuthorizedClient` annotation.

   The authorization grant type of client registration should be `authorization_code`, and is not yet authorized.

2. Resolve the authorized client parameter.

   `OAuth2AuthorizedClientArgumentResolver` will resolve the parameter with annotation `@RegisteredOAuth2AuthorizedClient` before executing the method.

     ```java
     public Object resolveArgument(MethodParameter parameter,
                                 @Nullable ModelAndViewContainer mavContainer,
                                 NativeWebRequest webRequest,
                                 @Nullable WebDataBinderFactory binderFactory) {
     
     // omitted
     
     // the authorizedClientManager is the instance of DefaultOAuth2AuthorizedClientManager
     return this.authorizedClientManager.authorize(authorizeRequest);
     }
     ```

3. Authorize client

   `OAuth2AuthorizedClientArgumentResolver` relies on `DefaultOAuth2AuthorizedClientManager`, which is the implementation  of interface `OAuth2AuthorizedClientManager`.
   the `DefaultOAuth2AuthorizedClientManager` will use `DelegatingOAuth2AuthorizedClientProvider` implementation of interface `OAuth2AuthorizedClientProvider` by default.

   The follow is a blocking implementation, and of course there are also reactive version.
   |  Implementation of `OAuth2AuthorizedClientProvider`   | Description  |
   |  ----  | ----  |
   | `DelegatingOAuth2AuthorizedClientProvider`  | Assign to a special client authorization provider based on grant type |
   | `AuthorizationCodeOAuth2AuthorizedClientProvider`  | Authorization grant type for `Authorization Code` flow |
   | `ClientCredentialsOAuth2AuthorizedClientProvider`  | Authorization grant type for `Password` flow |
   | `PasswordOAuth2AuthorizedClientProvider`  | Authorization grant type for `Password` flow |
   | `RefreshTokenOAuth2AuthorizedClientProvider`  | Authorization grant type for `Refresh Token` flow |

     ```java
     public OAuth2AuthorizedClient authorize(OAuth2AuthorizeRequest authorizeRequest) {
     
     // omitted
     
     try {
         // this authorizedClientProvider is the instance of DelegatingOAuth2AuthorizedClientProvider
         authorizedClient = this.authorizedClientProvider.authorize(authorizationContext);
     } catch (OAuth2AuthorizationException ex) {
         this.authorizationFailureHandler.onAuthorizationFailure(
                 ex, principal, createAttributes(servletRequest, servletResponse));
         throw ex;
     }
     
     // omitted
     
     return authorizedClient;
     }
   ```

4. Throw `ClientAuthorizationRequiredException` exception

   The implementation `AuthorizationCodeOAuth2AuthorizedClientProvider` will eventually be executed.
   ```java
     public OAuth2AuthorizedClient authorize(OAuth2AuthorizationContext context) {
         Assert.notNull(context, "context cannot be null");
     
         if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(context.getClientRegistration().getAuthorizationGrantType()) &&
                 context.getAuthorizedClient() == null) {
             // ClientAuthorizationRequiredException is caught by OAuth2AuthorizationRequestRedirectFilter 
             // which initiates authorization
             throw new ClientAuthorizationRequiredException(context.getClientRegistration().getRegistrationId());
         }
         return null;
     } 
    ```

5. Catch the exception and re-resolve the oauth2 authorization request

   `OAuth2AuthorizationRequestRedirectFilter` filter will catch the `ClientAuthorizationRequiredException` exception, and use special client registration id to resolve the request.
   This is the second time this method `OAuth2AuthorizationRequestResolver.resolve(javax.servlet.http.HttpServletRequest, java.lang.String)` is executed, [here][client-exception-uri-request-exec] is the code executed.
    ```java
     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
           throws ServletException, IOException {
    
       /// The normal OAuth2 request processing code is omitted
    
       try {
           filterChain.doFilter(request, response);
       } catch (IOException ex) {
           throw ex;
       } catch (Exception ex) {
           // Check to see if we need to handle ClientAuthorizationRequiredException
           Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(ex);
           ClientAuthorizationRequiredException authzEx = (ClientAuthorizationRequiredException) this.throwableAnalyzer
               .getFirstThrowableOfType(ClientAuthorizationRequiredException.class, causeChain);
           if (authzEx != null) {
               try {
                   OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestResolver
                        .resolve(request, authzEx.getClientRegistrationId());
                   if (authorizationRequest == null) {
                       throw authzEx;
                   }
                   this.sendRedirectForAuthorization(request, response, authorizationRequest);
                   this.requestCache.saveRequest(request, response);
               } catch (Exception failed) {
                   this.unsuccessfulRedirectForAuthorization(request, response, failed);
               }
               return;
           }
    
           if (ex instanceof ServletException) {
               throw (ServletException) ex;
           } else if (ex instanceof RuntimeException) {
               throw (RuntimeException) ex;
           } else {
               throw new RuntimeException(ex);
           }
       }
    }
    ```
       
### Triggered by `WebClient`

This is more practical way to easily initiate a reference to the OAuth2 client in any other place.
Add `ServletOAuth2AuthorizedClientExchangeFilterFunction` or `ServerOAuth2AuthorizedClientExchangeFilterFunction` filter to the `WebClient` instance. Of course, 
each filter function needs `DefaultOAuth2AuthorizedClientManager` to construct, so the `AuthorizationCodeOAuth2AuthorizedClientProvider` can be enabled.

You can refer [WebClient integration for Servlet Environments][oauth2Client-webclient-servlet] for more detail about configuration.


## Why to call `OAuth2AuthorizationRequestResolver#resolve` method two times

In fact, a client registration will only be called once. Two times means at least there are two client registrations to authorize,
**spring-security-oauth2-client** provides a mechanism, when the `ClientAuthorizationRequiredException` exception is caught,
it can try again to resolve whether a request is an OAuth2 authorization code request.
This can ensure that the OAuth2 client can normally initiate the OAuth2 authorization code process.
Please note the client authorization types we are discussing are all authorization code type.

[end-user-uri-request-exec]: https://github.com/spring-projects/spring-security/blob/2975923a1d213a529b2b65fef4adcea8e8ae8f4b/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizationRequestRedirectFilter.java#L149-L153

[client-exception-uri-request-exec]: https://github.com/spring-projects/spring-security/blob/2975923a1d213a529b2b65fef4adcea8e8ae8f4b/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizationRequestRedirectFilter.java#L165-L189

[oauth2Client-webclient-servlet]: https://docs.spring.io/spring-security/site/docs/5.4.5/reference/html5/#oauth2Client-webclient-servlet
