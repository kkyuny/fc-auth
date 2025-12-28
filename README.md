## 학습 내용 정리

## 인증/인가 및 접근 제어
- **인증(Authentication)**
    - 사용자가 누구인지 확인하는 과정
    - 예: 로그인 시 ID/PW 검증, 소셜 로그인, JWT 토큰 발급
- **인가(Authorization) 정의**
    - 인증된 사용자가 어떤 자원에 접근할 수 있는지 결정하는 과정
    - Role 기반 접근 제어(RBAC)를 주로 사용

## 인증/인가 데이터 흐름 요약
1. 로그인 → Employee 인증 → JWT 발급
2. API 접근 → 토큰 검증 → 권한 확인 → 접근 허용/거부
3. 필요 시 App2App 토큰 사용 → 시스템 간 안전한 호출(시스템 간 인증/인가 수행)

## Spring Security JWT 필터 흐름 정리
## 1. SecurityConfig 역할
-`@EnableWebSecurity` + `SecurityFilterChain` 설정으로 **보안 필터 체인 구성**

### 주요 설정
- **CSRF 비활성화**: `http.csrf(AbstractHttpConfigurer::disable);` → JWT 인증은 서버 세션을 사용하지 않으므로 CSRF 토큰 필요 없음. 공격 방어용 CSRF 체크 비활성화
- **CORS 기본 설정**: `http.cors(Customizer.withDefaults());` → 다른 도메인에서 API 호출 시 브라우저가 허용하도록 CORS 정책 적용
- **세션 상태 무상태(stateless)**: JWT 인증이므로 서버 세션 사용 안 함 → Stateless로 설정
- **폼 로그인 비활성화**: `http.formLogin(AbstractHttpConfigurer::disable);` → Spring Security 기본 로그인 폼 사용 안 함, JWT 인증만 사용
- **JWT 필터 등록**
  ```
  http.addFilterBefore(new JwtAuthFilter(...), UsernamePasswordAuthenticationFilter.class);
  ```
  - UsernamePasswordAuthenticationFilter 앞에 JWT 인증 필터를 끼움 → 실제 JWT 토큰 검증, SecurityContext 설정
- **허용 URL 설정**: `AUTH_ALLOWLIST`만 허용
- **인증 필요 URL 설정**: `AUTH_ALLOWLIST`의 허용 URL 외 인증이 필요한 URL을 요구
- **커스텀 예외 처리**: 인증 실패 시 `CustomAuthenticationEntryPoint` 등 사용이 가능함.
- **정리**: SecurityConfig는 **필터 체인에서 어떤 필터를 어떤 순서로 실행할지**, **어떤 URL은 허용/거부할지**를 정의


## 2. JwtAuthFilter 역할
-`OncePerRequestFilter` 상속 → 요청당 한 번 실행

### 동작 과정
1) **Authorization 헤더 확인**
    - "Bearer "로 시작하면 JWT 토큰 추출
2) **KakaoService 호출**
   - 토큰 기반으로 Kakao 사용자 정보 조회
3) **EmployeeRepository 확인**
   - DB에 존재하면 Employee 객체 조회
4) **권한(ROLE) 설정**
   - 일반 유저 → `ROLE_USER`
   - HR → `ROLE_ADMIN` 추가
5) **Spring SecurityContext에 인증 정보 저장**
  ```
  SecurityContextHolder.getContext().setAuthentication(authentication);
  ```
  - 이후 @AuthenticationPrincipal(컨트롤러 파라미터), SecurityContextHolder 등으로 접근 가능
6) **필터 체인 계속 진행**
  ```
  filterChain.doFilter(request, response);
  ```
**정리**: JwtAuthFilter는 모든 요청에서 JWT를 확인하고 인증 정보를 SecurityContext에 올리는 실질적 인증 처리 필터.

## 3. 요청 흐름
```
클라이언트 요청
      │
      ▼
서블릿 컨테이너 (Tomcat)
      │
      ▼
Security FilterChain (필터 순서대로 실행)
 ├─ JwtAuthFilter  ← JWT 인증, SecurityContext 설정(OncePerRequestFilter로 1번만 실행)
 ├─ UsernamePasswordAuthenticationFilter (사용안함)
 ├─ 기타 Spring Security 필터 (CSRF, CORS 등)
      │
      ▼
DispatcherServlet
      │
      ▼
HandlerInterceptor
```

