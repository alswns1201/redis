# Spring Redis Integration Example

이 프로젝트는 Spring Boot 애플리케이션에서 Redis를 활용하는 다양한 방법을 보여주는 예제입니다. 비동기 이메일 전송 시 분산 락(Redisson)을 사용하는 방법과 Redis를 키-값(Value) 및 리스트(List) 캐시로 활용하는 방법을 포함하고 있습니다.

## 🚀 주요 기능

*   **Redisson 분산 락**: Redisson을 이용하여 이메일 주소별로 분산 락을 구현하여 이메일 중복 전송을 방지합니다. `@Async` 어노테이션을 활용하여 비동기적으로 이메일을 전송합니다.
*   **Redis Value Cache**: Redis를 단순한 키-값 캐시로 사용하여 `PersonDTO` 객체를 저장하고 조회, 삭제하는 기능을 제공합니다.
*   **Redis List Cache**: Redis List 자료구조를 활용하여 `PersonDTO` 리스트를 저장하고, 범위 조회, 마지막 요소 조회, 특정 범위 유지(trim) 기능을 제공합니다.
*   **비동기 처리**: `@Async` 어노테이션과 `ThreadPoolTaskExecutor` 설정을 통해 비동기 작업을 효율적으로 처리합니다.

## 🛠️ 기술 스택

*   Spring Boot
*   Redis
*   Redisson: 분산 락 구현을 위한 Redis 클라이언트
*   Spring Data Redis: Redis를 캐시로 사용하기 위한 Spring 통합
*   Lombok: 보일러플레이트 코드 감소

## 📦 프로젝트 구조
```
src/main/java/com/spring/redis
├── config
│ └── RedisConfiguration.java # Redis, Redisson 클라이언트 및 비동기 Executor 설정
├── controller
│ └── PersonController.java # PersonDTO 캐싱 관련 REST API
├── dto
│ ├── PersonDTO.java # Person 데이터 전송 객체
│ └── RangeDTO.java # Redis List 범위 조회/trim을 위한 객체
├── service
│ ├── RedisListCache.java # Redis List operations 서비스
│ ├── RedisValueCache.java # Redis Value operations 서비스
│ └── email
│ └── EmailService.java # Redisson 분산 락을 이용한 이메일 전송 서비스
└── SpringRedisApplication.java # Spring Boot 메인 애플리케이션
```
## ⚙️ 설정

### 1. Redis 서버 실행

로컬 또는 Docker를 이용하여 Redis 서버를 실행해야 합니다.

```bash
docker run -d --name my-redis -p 6379:6379 redis
```

# Redis Host 및 Port (Redisson과 Spring Data Redis 모두 사용)
spring.redis.host=localhost
spring.redis.port=6379

# 비동기 이메일 전송을 위한 Thread Pool 설정
# (RedisConfiguration.java의 EmailExecutor 빈 정의에 사용됨)
# 필요에 따라 tuning 가능


### Redisson 및 Spring Data Redis 설정

com.spring.redis.config.RedisConfiguration 클래스에서 RedissonClient, RedisTemplate, 그리고 @Async를 위한 EmailExecutor를 설정합니다.
RedissonClient: localhost:6379 단일 서버 구성으로 Redisson을 초기화합니다.
RedisTemplate: Lettuce 클라이언트를 사용하여 localhost:6379에 연결하고, String 키와 Object 값을 직렬화하여 Redis에 저장하도록 설정됩니다.
EmailExecutor: 이메일 서비스의 비동기 작업을 위한 전용 스레드 풀을 정의합니다.

### 🚀 API 엔드포인트

1. 이메일 서비스 (EmailService) : 이메일 전송 기능은 직접적인 HTTP 엔드포인트로 노출되지 않고, 내부적으로 다른 서비스에서 호출되거나 테스트 목적으로 사용될 수 있습니다. @Async로 동작하며, Redisson 분산 락을 통해 동일 이메일 주소에 대한 동시 전송을 방지합니다.
2. Person 캐싱 서비스 (PersonController)
기본 URL: /api/person
POST /api/person
설명: PersonDTO 객체를 Redis Value 캐시에 저장합니다.
요청 바디:
```code
JSON
{
  "id": "person-123",
  "name": "John Doe",
  "age": 30
}
```
GET /api/person/{id}
설명: Redis Value 캐시에서 특정 id에 해당하는 PersonDTO를 조회합니다.
DELETE /api/person/{id}
설명: Redis Value 캐시에서 특정 id에 해당하는 PersonDTO를 삭제합니다.
POST /api/person/list/{key}
설명: List<PersonDTO>를 Redis List 캐시에 key에 연결하여 저장합니다. (leftPush 사용)
요청 바디:
```code
JSON
[
  { "id": "p1", "name": "Alice", "age": 25 },
  { "id": "p2", "name": "Bob", "age": 30 }
]
```
GET /api/person/list/{key}
설명: Redis List 캐시에서 key에 해당하는 리스트의 특정 범위(from, to)에 있는 PersonDTO들을 조회합니다.
요청 바디:
```code
JSON
{
  "from": 0,
  "to": 1
}
```
GET /api/person/list/last/{key}
설명: Redis List 캐시에서 key에 해당하는 리스트의 마지막 요소를 조회하고 삭제합니다 (rightPop).
DELETE /api/person/list/{key}
설명: Redis List 캐시에서 key에 해당하는 리스트의 요소를 특정 범위(from, to)를 제외하고 삭제합니다 (trim).
요청 바디:
```code
JSON
{
  "from": 0,
  "to": 0
}
```
