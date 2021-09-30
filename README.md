# my_springsecurity_framework
+ spring을 공부하고 첫 토이 프로젝트인 starbooks 프로젝트를 진행하면서 사용자 인증, 인가 부분 즉 로그인 기능을 구현하기 위해 spring security를 공부하여 내 스타일로 코드를 썼다.
+ spring security는 기본적으로 세션기반으로 설계되어있지만, 이 프로젝트에서는 토큰 기반 인증인 jwt토큰을 이용하였다. + (OAuth 2.0 예정)   



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



## 개선해야할 점
+ refresh token을 redis로 관리
+ refresh token에도 만료시간 설정하여 만료된 토큰 자동 삭제
+ jwt token 로그아웃 기능 구현(api)
+ jwt 검증 실패관련 예외처리를 클라이언트한테 친절하게 내려줄지? 
ex)잘못된 JWT 서명입니다, 지원되지 않는 JWT 토큰입니다. => 맞는 방법일지 의문     
