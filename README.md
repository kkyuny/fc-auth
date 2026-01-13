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

## JWT 토큰
- **JWT(Json Web Token)**: JSON 기반의 토큰
- 헤더(Header) + 페이로드(Payload) + 서명(Signature)으로 구성
- 서버가 발급 → 클라이언트/앱이 들고 다니며 서명으로 검증
```java
return Jwts.builder()
        .setSubject("userId123") // sub: 토큰 주체 (보통 사용자 ID)
        .setIssuer("fc-auth")    // iss: 토큰 발급자
        .setIssuedAt(new Date()) // iat: 토큰 발급 시각
        .setExpiration(          // exp: 토큰 만료 시각
                new Date(System.currentTimeMillis() + EXP_MS)
        )
        .claim("role", "ADMIN") // custom claim: 사용자 권한
        .claim("appId", 10L)    // custom claim: 애플리케이션 ID
        .signWith(SECRET_KEY)   // 서명: 위변조 방지용 키
        .compact();             // JWT 문자열 생성
```
## [5-1 ~ 2] Spring Security / JWT 필터 정리
### 1. SecurityConfig 역할
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


### 2. JwtAuthFilter 역할
-`OncePerRequestFilter` 상속 → 요청당 한 번 실행
- 실질적 인증 처리 담당
    - 모든 요청에서 JWT 토큰 확인 → 주체(사용자 또는 앱) 식별 
- SecurityContext에 Authentication(인증정보) 객체 등록
    - 이후 @AuthenticationPrincipal, SecurityContextHolder 등을 통해 컨트롤러에서 사용자/앱 정보 접근 가능
- JWT 기반 인증 구조에서는 모든 요청에서 토큰 검증 필수

### 3. JwtAuthFilter 동작 과정
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

### 4. 요청 흐름
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

### 5. 정리
- Spring Security는 `설정(SecurityConfig) + 인증/인가 처리 로직(필터, 토큰(jwt 등등) 인증)` 중심으로 동작.
- SecurityConfig
    - 필터 체인 정의
    - URL 접근 제어(허용/거부)
    - CSRF, CORS, 세션 정책 등 환경 설정
    - 필터 순서와 위치 결정
- JWT 유틸/서비스
    - JWT 생성, 서명, 만료 관리
    - 토큰 payload 검증, claim 추출
    - App2App/사용자 토큰 모두 처리
- JwtAuthFilter (혹은 커스텀 필터)
    - 요청당 JWT 검증
    - 주체 식별(사용자 또는 앱)
    - 권한 설정 → SecurityContext 등록
    - 필터 체인 계속 진행

## [5-3] RBAC(Role-Based Access Control)

### RBAC 개요
- 사용자에게 **Role(역할)** 을 부여
- 리소스 접근 여부는 **Role 기준**으로 판단
- Spring Security는 `GrantedAuthority` 기반으로 RBAC 구현

### Role 표현 방식
- Role은 내부적으로 **GrantedAuthority** 로 관리
- 관례적으로 `ROLE_` 접두사 사용
  - `ROLE_USER`
  - `ROLE_ADMIN`
- `hasRole("USER")` → 내부 비교값: `ROLE_USER`

### JwtAuthFilter 내 RBAC 설정
**1) 권한 목록 생성**
```java
List<GrantedAuthority> authorities = new ArrayList<>();
authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
```
- 인증된 모든 사용자에게 기본 USER 권한 부여

**2) 조건부 Role 추가 (다중 Role)**
```java
if (Employee.isHR(employee)) {
    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
}
```
- 도메인 로직 기반 Role 판별
- HR 직원은 USER + ADMIN Role 동시 보유

**3) Authentication 객체와 RBAC**
```java
Authentication authentication =
    new TestingAuthenticationToken(
        employee.getFirstName(),
        "password",
        authorities
    );
```
- 인증이 된 사용자에게 권한과 함께 Spring Security가 이해하는 Authentication 객체를 생성한다.
- 비밀번호는 JWT 구조상 의미 없음

### SecurityContext와 인가 흐름
```java
SecurityContextHolder.getContext().setAuthentication(authentication);
```
- JWT 인증 필터에서 적용
- 이후 모든 계층에서 Role 기반 접근 제어 가능
    - URL 보안 설정
        ```java
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers(AUTH_ALLOWLIST).permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        );
        ```
        - /admin/**에는 ADMIN만 허용
    - 메서드 보안
        ```java
        @PreAuthorize("hasRole('ADMIN')")
        public void deleteUser(Long userId) {
            ...
        }
        ```
- 컨트롤러에서 사용자 정보 접근 가능
    ```java
    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal CustomUser user) {
        return UserDto.from(user);
    }
    ```
    - SecurityContext에 저장된 현재 로그인 사용자 객체를 바로 꺼내 쓰는 것
    - SecurityContextHolder.getContext().getAuthentication().getPrincipal()의 축약이다.

### 정리
- RBAC 핵심 데이터: GrantedAuthority 목록
- JwtAuthFilter의 역할
    - 사용자 식별
    - Role 판별
    - SecurityContext에 Role 포함 Authentication 저장
    - 이후 Spring Security가 자동으로 인가 처리

## [5-4 ~ 5] App2App 인증
### 개요: 사용자 없이 애플리케이션 간에 인증·인가를 수행하는 구조
### 인증 흐름
``` mermaid
sequenceDiagram
    participant App1
    participant AuthServer as Auth Server
    participant App2

    App1->>AuthServer: (1) client_id / secret
    AuthServer-->>App1: (2) JWT 발급
    App1->>App2: (3) Authorization: Bearer JWT
    App2->>AuthServer: (4) JWT 검증
    App2-->>App1: (5) 기능 제공
```

## [7-1] Redis 
### Spring Cache Annotations
- Cache 어노테이션을 사용하면 RedisTemplate 없이 Redis를 동작시킬 수 있다.

| 어노테이션 | 동작 요약 | 목적 / 사용 상황 | 간단 예제 코드 |
|----------|----------|----------------|--------------|
| `@Cacheable` | 캐시에 값이 있으면 메서드 실행 X, 없으면 실행 후 저장 | 조회 최적화, DB 호출 줄이기 | `@Cacheable(value="users", key="#id")`<br>`public User getUser(Long id)` |
| `@CachePut` | 메서드를 항상 실행 후 결과를 캐시에 저장 | 캐시 갱신, 데이터 동기화 | `@CachePut(value="users", key="#user.id")`<br>`public User updateUser(User user)` |
| `@CacheEvict` | 캐시에서 특정 데이터 삭제 | 삭제, 캐시 초기화 | `@CacheEvict(value="users", key="#id")`<br>`public void deleteUser(Long id)` |
| `@Caching` | 여러 캐시 동작을 한 메서드에 적용 | 복합적인 캐시 처리 | `@Caching(` <br>` put=@CachePut(value="users", key="#user.id"),`<br>` evict=@CacheEvict(value="allUsers", allEntries=true)`<br>`)` |

## [7-2] Threshold & Throttling

### 사용이유
- 서버 과부하 방지
- 악성 트래픽 / 남용 방지
- 자원 공정 분배
- 비용 제어 (외부 API, DB 호출 등)

### Threshold
- 어떤 행위를 허용할 수 있는 **최대 한계값**
- 보통 **시간 단위당 허용량**으로 정의됨
- 예시
    - API 호출: 분당 100회
    - 로그인 시도: 5회 초과 시 차단
    - 메시지 전송: 초당 10건
        > "이 기준(threshold)을 넘으면 제한한다"

### Throttling
- Threshold를 **초과했을 때 적용하는 제어 행위**
- 요청을 제한하거나 거부하거나 속도를 늦춤

### Token Bucket
- Threshold = 버킷에 담을 수 있는 **최대 토큰 수**
- 요청 1건 = 토큰 1개 소비
- 토큰이 0이 되면 → Throttling 발생 (요청 제한)

### 예시코드
``` java
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomRateLimiter {
    private final Map<String, Bucket> keyToBucketMap = new ConcurrentHashMap<>();
    private final Duration REFILL_PERIOD_ONE_MINUTE = Duration.ofMinutes(1L);

    private final AppRoleRepository appRoleRepository;

    public boolean tryConsume(Long appId, Long apiId){
        String key = appId.toString() + ":" + apiId.toString();
        Bucket bucket = keyToBucketMap.computeIfAbsent(key, k-> createBucket(appId, apiId));

        log.info(String.format("throttling : %s count ++1", key));
        return bucket.tryConsume(1);
    }

    public LocalBucket createBucket(Long appId, Long apiId){
        Integer threshold = appRoleRepository.findByAppIdAndApiId(appId, apiId).getThreshold();
        return Bucket.builder()
                .addLimit(BandwidthBuilder.builder().capacity(threshold).refillIntervally(threshold, REFILL_PERIOD_ONE_MINUTE).build())
                .build();
    }
}
```
- 버킷을 생성하며 Threshold 설정
- 컨슘에서 Threshold 초과 시 false를 반환하여 Throttling을 수행
