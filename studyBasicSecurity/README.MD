# 스프링 시큐리티 기본 API 공부

### 스프링 시큐리티 의존성 추가시 일어나는 일들

* 서버 기동시 스프링 시큐리티의 초기화 작업 및 보안 설정.
* 별도의 설정이나 구현을 하지 않아도  
  기본적인 웹 보안 기능이 현재 시스템에 연동되어 작동함
    1. 모든 요청은 인증이 되어야 자원에 접근이 가능
    2. 인증 방식은 폼 로그인 방식과 httpBasic 로그인 방식을 제공
    3. 기본 로그인 페이지 제공
    4. 기본 계정 한 개 제공

    * username : user
    * password : 무작위 문자열
    ```
    2021-04-11 17:06:47.362  INFO 5156 --- [  restartedMain] .s.s.UserDetailsServiceAutoConfiguration :
    Using generated security password: c562795a-04ac-4300-a234-e3327a7b2dd2
    ```

```java
//자동 웹 보안 설정 클래스
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DefaultAuthenticationEventPublisher.class)
@EnableConfigurationProperties(SecurityProperties.class)
@Import({SpringBootWebSecurityConfiguration.class, WebSecurityEnablerConfiguration.class,
	SecurityDataConfiguration.class})
public class SecurityAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(AuthenticationEventPublisher.class)
	public DefaultAuthenticationEventPublisher authenticationEventPublisher(
		ApplicationEventPublisher publisher) {
		return new DefaultAuthenticationEventPublisher(publisher);
	}

}
```

일반적으로 부트에서 시큐리티를 사용시에는 해당 자동 설정 클래스를 제외처리 하지 않고 개발자가 설정한 옵션이 먼저 적용되도록 설정함

그 이유로는 자동 설정 기능을 제외시에 따로 설정 및 선언해야 할게 너무 많음.

기본 설정만으로도 훌륭한 보안 체계를 가짐.

그럼에도 불구하고 따로 설정을 기술하는 이유는  
해당 어플리케이션에 맞게 커스터마이징과 미흡한 부분(DB 연동, 계층형 권한 등등)으로 인해서.


---

#### 사용자 정의 보안 기능

SecurityConfig (상속)-> __WebSecurityConfigurerAdapter__ -> __HttpSecurity__ (인증 API, 인가 API)

기본 __HttpSecurity__

```java
protected void configure(HttpSecurity http)throws Exception {
	this.logger.debug("Using default configure(HttpSecurity). "
	+"If subclassed this will potentially override subclass configure(HttpSecurity).");
	http.authorizeRequests((requests)->requests.anyRequest().authenticated());
	http.formLogin();
	http.httpBasic();
}
```

---

#### 폼 로그인 관련 설정

`http.formLogin()`

```java
protected void configure(HttpSecurity http)throws Exception {
	http
        .formLogin()                            // 폼 로그인 사용 설정
	    .loginPage("/login.html")               // 사용자 정의 로그인 페이지
	    .defaultSuccessUrl("/home")             // 로그인 성공 후 이동 페이지
        .failureUrl("/login.html?error=true")   // 로그인 실패 후 이동 페이지
        .usernameParameter("username")          // 아이디 파라미터명 설정
        .passwordParameter("password")          // 패스워드 파라미터명 설정
        .loginProcessingUrl("/login")           // 로그인 폼 ActionUrl
        .successHandler(LoginSuccessHandler())  // 로그인 성공 후 핸들러
        .failureHandler(loginFailureHandler())  // 로그인 실패 후 핸들러
}
```
---
#### 인증과 권한

* Authentication 인증  
  애플리케이션에서 인증 절차를 통해 권한을 확인하는 과정
* Authorization 권한  
  요청에 대한 권한을 가지고 있는가 확인 하는 과정.
  
##### Authentication

* `principal` - 서비스에 접근하는 유저 정보 객체
* `Authentication` - Spring Security 에서 한 유저의 인증 정보를 가지고 있는 객체로  
  사용자가 인증 과정을 성공적으로 마칠 시.  
  Spring Security는 사용자의 정보 및 인증 성공 여부를 가지고  
  `Authentication` 객체를 생성 후 보관한다.
* `SecurityContextHolder` - `Authentication` 객체를 보관하는 곳.  
  어플리케이션 어디서는 접근 가능.
* `UserDetails` - 일반 서비스의 사용자 객체를  
  Spring Security에서 사용하는 사용자 객체와 연결 및 변환 해주는 어댑터 역활.
* `UserDetailsService` - Spring Security에서 로그인시에 전달된 정보를 기반으로  
  DB에서 유저를 가져오는 서비스를 하는 인터페이스.
* `GrantedAuthority` - 사용자에게 주어진 어플리케이션 사용 권한 객체.
* `PasswordEncoder` - DB에 사용자의 정보 저장시 비밀번호를 암호화  
  인증시 입력된 비밀번호와 저장된 비밀번호 비교하는 객체.
  
---

#### 로그아웃 

`LogoutFilter` 를 통해 처리됨.

`Authentication` 인증 객체를 가져와서 `SecurityContextLogoutHandler` 를 통해 처리.

`SecurityContextLogoutHandler`는 주로
1. 세션 무효화
2. 쿠키 삭제
3. `SecurityContextHolder.clearContext()` 호출  
   인증 관련 저장 객체에서 인증 객체 비워줌.
   
처리 완료후 `SimpleUrlLogoutSuccessHandler`를 통해 리다이렉트 처리.

```java
protected void configure(HttpSecurity http) throws Exception {
	http.logout()                                       // 로그아웃 처리
        .logoutUrl("/logout")                           // 로그아웃 처리 URL
        .logoutSuccessUrl("/login")                     // 로그아웃 성공 후 이동페이지
        .deleteCookies("JSESSIONID","remember-me")      // 로그아웃 후 쿠키 삭제
        .addLogoutHandler(logoutHandler())              // 로그아웃 핸들러
        .logoutSuccessHandler(logoutSuccessHandler())   // 로그아웃 성공 후 핸들러
}
```

---

#### Remember Me 인증

세션 만료되고 웹 브라우저 종료 후에도 사용자를 기억하는 기능
 
처리 절차  
1. `Remember-Me` 쿠키 생성
2. Http 요청을 통한 토큰 기반 인증 및 유효성 검사
3. 검증 완료 시 사용자 로그인.

라이프 사이클
* 인증 성공  
   `Remember-Me` 쿠키 설정
* 인증 실패  
  쿠키 존재시 쿠키 무표화
* 로그아웃
  쿠키 존재시 쿠키 무효화
  
```java
protected void configure(HttpSecurity http) throws Exception {
	http.rememberMe()
            .rememberMeParameter("remember")        // 기본 파라미터 명은 remember-me
            .tokenValiditySeconds(3600)             // 기본값은 14일
            .alwaysRemember(true)                   // 리멤버 미 기능을 활성화하지 않아도 항상 실행
            .userDetailsService(userDetailsService)
}
```

##### RememberMeAuthenticationFilter

Request   
-> RememberMeAuthenticationFilter   
-> RememberMeServices  
-> Token Cookie 추출  
-> Token 유무 확인 -> 없을시 다음 필터로   
-> Decode Token -> 토큰 불일치시 Exception 발생  
-> Token 일치 확인 -> 불일치시 Exception 발생   
-> User 계정 확인 -> 없을시 Exception 발생   
-> 새로운 Authentication 생성 (RememberMeAuthenticationToken)   
-> AuthenticationManager

필터의 처리 조건
1. `Authentication` 인증 객체가 `null`일 경우.  
   `SecurityContext` 인증 객체가 `null` 인 경우  
   -> 세션 만료 혹은 브라우저 종료되어 세션 종료시.
2. `Remember-Me` 토큰이 발급되어 쿠키를 가져오는 경우.

`RememberMeServices`
* `TokenBasedRememberMeServices`  
  메모리에서 토큰 저장하여 비교 및 인증 처리
* `PersistentTokenBasedRememberMeServices`  
  DB에 토큰 저장하여 비교 및 인증 처리
  
---
#### 익명사용자

`AnonymousAuthenticationFilter` 를 통해 익명 사용자 처리  
(별도의 익명사용자 Authentication 객체 사용)

* 익명사용자와 인증 사용자를 구분해서 처리하기 위해 사용
* 인증 여부 구현시 `isAnonymous()` 와 `isAuthenticated()`로 구분하여 사용
* 인증 객체를 세션에 저장하지 않음.

---
#### 세션 관련

__동시 세션 제어__  
동일한 계정에 대한 제어  
최대 세션 허용 개수 초과  
1. 이전 사용자 세션 만료
2. 현재 사용자 인증 실패

```java
protected void configure(HttpSecurity http) throws Exception {
	http.sessionManagement()
        .maximumSessions(1)                 // 세션 최대 허용 가능 수, -1 : 무제한
        .maxSessionsPreventsLogin(true)     // 동시 로그인 차단, false : 기존 세션 만료(기본값)
        .invalidSessionUrl("/invalid")      // 세션 유효하지 않을 시 이동 페이지
        .expiredUrl("/expired")             // 세션 만료후 이동 페이지
}
```

__세션 고정 보호__
인증 성공시 새로운 세션 생성하여 고정 세션 공격 방지.

```java
protected void configure(HttpSecurity http) throws Exception {
	http.sessionManagement()
        .sessionFixation().changeSessionId() //기본값
                                             // none, migrateSession, newSession
}
```

__세션 정책__

세션 관리 정책
```java
protected void configure(HttpSecurity http) throws Exception {
	http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.If_Required)
        // Always - 시큐리티가 항상 세션 생성
        // If_Required - 시큐리티가 필요 시 생성 (기본 값)
        // Never - 시큐리티가 생성하지 않지만 존재시 사용
        // Stateless - 시큐리가 생성하지 않고 사용하지도 않음
}
```
