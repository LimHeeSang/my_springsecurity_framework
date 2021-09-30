# Spring Security를 이용한 회원가입 및 로그인 API (My Style Code)
+ spring을 공부하고 첫 토이 프로젝트인 starbooks 프로젝트를 진행하면서 사용자 인증, 인가 부분 즉 로그인 기능을 구현하기 위해 spring security를 공부하여 내 스타일로 코드를 썼다.
+ spring security는 기본적으로 세션기반으로 설계되어있지만, 이 프로젝트에서는 토큰 기반 인증인 jwt토큰을 이용하였다. + (OAuth 2.0 추가 예정)   



## 특징
+ spring security는 기본적으로 세션기반으로 설계되어있지만, 이 프로젝트에서는 jwt토큰을 이용하였다.
+ (토큰을 이용한 검증, 발급, 인증수행 과정이 필요하기 때문에 세션기반의 인증방식과 살짝 차이가 있다.)
+ jwt 토큰을 이용하여 사용자 로그인을 통한 인증을 수행할 수 있고, 각 서버에서 부여한 권한에 따라 요청할 수 있는 api를 제한할 수 있다.
+ 인증을 위해 필요한 access token과 별개로 만료된 토큰을 재발급 받기 위해서는 refresh token인 재발급용 토큰을 따로 발급해준다. (보안적인 요소 고려)
+ refresh token을 따로 관리하기 위해 redis를 많이 쓴다고 알고있는데, 아직 redis를 공부해본 적이 없어서 rdb로 기능만 구현했다. 나중에 공부하게되면 리팩토링 할 계획이다. 
+ 각 상황에 따른 예외처리는 서비스마다 상이하므로 간단하게 처리했다.(각 프로젝트 주제에 맞는 예외처리가 필요하다.)   



## 구조
+ spring security는 필터기반으로 아키텍쳐가 구성되어 있다.
+ 요청이 오면 필터가 가로채서 미인증 객체(UsernamePasswordAuthenticationToken)를 생성해서 AuthenticationManger<<interface>>한테 넘기면 AuthenticationManger에서 인증을 처리할 수 있는 
AuthenticationProvider를 찾아서 미인증 객체를 또 넘긴다. AuthenticationProvider는 UserDetailsService를 이용하여 인증(authenticate)과정을 수행 후 인증 된 객체를 생성하여 Security 
Context에 저장 후 관리한다. (기본적으로 세션방식이 이렇다.)
+ Jwt token 방식에서는 토큰을 생성하고 검증하고 인증객체를 생성해주는 JwtTokenProvider를 만들어서 이용한다. JwtTokenProvider역시 UserDetailsService를 이용하고 token을 검증 후에 인증 
된 객체를 생성하여 Security Context에 저장 후 관리한다.
+ 인증이 실패할 때 예외처리를 위한 핸들러를 정의하여 활용 가능하다.   


## 만났던 에러
+ Spring Security의 hasRole함수는 자동으로 주어진 인자에 'ROLE_'접두어를 붙이고 검사를 시작한다. 따라서 올바른 권한을 가지려면 USER가 아니라 ROLE_USER를 가져야 한다.(Enum 정의)     
  
+ 아래는 hasRole함수 내부이다. 
```java
private static String hasRole(String role) {
        Assert.notNull(role, "role cannot be null");
        if (role.startsWith("ROLE_")) {
            throw new IllegalArgumentException("role should not start with 'ROLE_' since it is automatically inserted. 
                                                Got '" + role + "'");
        } else {
            return "hasRole('ROLE_" + role + "')";
        }
    }
```
  
  + 해결
  1. hasRole 함수가 아닌 hasAuthority 함수를 사용한다.
  2. UserDetails를 구현한 Entity객체의 getAuthorities()함수에서 ROLE_를 덧붙여준다.
  ```java
@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
```
=> 나는 그냥 간단하게 역할을 정의해 놓은 Enum의 모든 변수이름에 ROLE_을 붙혔지만 나중에 변동사항 요청이 있을수도 있으니 정리해 뒀다.

  
## 개선해야할 점
+ refresh token을 redis로 관리
+ refresh token에도 만료시간 설정하여 만료된 토큰 자동 삭제
+ jwt token 로그아웃 기능 구현(api)
+ OAuth 2.0 인증 방법 추가하기
+ jwt 검증 실패관련 예외처리를 클라이언트한테 친절하게 내려줄지? 
ex)잘못된 JWT 서명입니다, 지원되지 않는 JWT 토큰입니다. => 맞는 방법일지 의문        


### 만났던 에러를 해결해주는데 도움을 얻은 곳
[깃허브 주소](https://github.com/koogk7/LoginApiForJwtAndSecurity#-jwt%EC%99%80-springsecurity%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EB%B0%8F-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-rest-api)      


### JWT token관련 개념과 보안 이슈관련 내용을 잘 정리해둔 곳(댓글)
[블로그 주소](https://blog.outsider.ne.kr/1160)

