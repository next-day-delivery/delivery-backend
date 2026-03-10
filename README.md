# Next Day Delivery Backend

## 📌 프로젝트 목적/상세

본 프로젝트는 사용자(CUSTOMER), 가게 주인(OWNER), 관리자(MANAGER, MASTER) 역할을 나누어 상품 주문, 결제, 배달 및 리뷰까지 아우르는 **종합 배달 플랫폼 백엔드
API 서비스**입니다.  
사용자의 주문 처리와 결제, 음식점/상점의 상품 및 주문 관리, 그리고 배달원 연계까지 지원하며 안정적이고 빠른 데이터 처리 및 AI 기반 응답/리뷰 연동 기능을 제공하는 것을 목표로 합니다.

## 👥 팀원 역할 분담

| 구분         | 김재현                                 | 최승원                                             | 예준성                                       | 임세희                                 | 나웅철                                 | 김준원                                           |
|------------|-------------------------------------|-------------------------------------------------|-------------------------------------------|-------------------------------------|-------------------------------------|-----------------------------------------------|
| 역할         | 팀장 및 Backend                        | Backend                                         | Backend                                   | Backend                             | Backend                             | Backend                                       |
| 담당 업무 및 기능 | 가게, 카테고리 도메인                        | 회원 도메인 및 인증·인가                                  | 상품 도메인 및 AI API 연동                        | 주문, 결제, 배송 도메인                      | 가게, 리뷰 도메인                          | 장바구니 도메인 및 인프라 구축                             |
| GitHub     | [RunSBS](https://github.com/RunSBS) | [chltjsdl0119](https://github.com/chltjsdl0119) | [gnoesnooj](https://github.com/gnoesnooj) | [Sehi55](https://github.com/Sehi55) | [No-366](https://github.com/No-366) | [kim-jun-won](https://github.com/kim-jun-won) |

## 🛠️ 기술 스택

### Backend

- **Language**: Java 21
- **Framework**: Spring Boot 3.5.11 (Spring Web, Spring Security, Spring Validation)
- **ORM / Database**: Spring Data JPA, QueryDSL, PostgreSQL
- **Authentication**: JWT (JJWT)
- **Database Migration**: Liquibase
- **AI Integration**: Google GenAI

### Infrastructure / DevOps

- **Containerization**: Docker, Docker Compose (PostgreSQL, Redis)
- **Build Tool**: Gradle

## 🏗️ 서비스 구성 및 실행 방법

### 1. 환경 변수 설정

프로젝트 최상단에 `.env` 파일을 생성하고 아래와 같이 필요한 환경 변수를 설정합니다.

```env
# PostgreSQL
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_password
POSTGRES_DB=your_db_name
POSTGRES_PORT=5432

# Redis
REDIS_PASSWORD=your_redis_password
REDIS_PORT=6379

# JWT & AI (필요 시 추가)
GENAI_API_KEY=your_google_genai_api_key
```

### 2. 인프라 로컬 실행 (Docker Compose)

데이터베이스(PostgreSQL)를 Docker 컨테이너로 백그라운드에서 실행합니다.

```bash
docker-compose up -d
```

### 3. 애플리케이션 실행

Local profile Spring Boot 애플리케이션을 실행합니다. (데이터베이스 테이블, 인덱스 생성 및 변경사항은 DB 실행 시 Liquibase에 의해 자동 마이그레이션 됩니다.)

## 📊 ERD

데이터베이스 구조는 크게 다음과 같은 주요 도메인/엔티티들로 구성되어 있습니다.

- **User**: `p_user` (고객, 점주, 매니저, 관리자), `p_user_address`
- **Store & Product**: `p_store`, `p_store_address`, `p_category`, `p_store_category`, `p_product`
- **Order Process**: `p_cart`, `p_cart_item`, `p_order`, `p_order_line`
- **Payment & Delivery**: `p_payment`, `p_delivery`
- **Review**: `p_review`
- **AI Engagement**: `p_ai_response`

*(※ 완성된 ERD 다이어그램 이미지 캡처본이나 링크를 아래에 첨부하세요)*

![ERD Image]()

## ⚙️ Architecture

### Infra Architecture

![Infra Architecture Image]()

### CI/CD Pipeline

![CI/CD Pipeline Image]()

## 📚 API 문서

- API 명세서는 Spring RestDocs를 통해 제공됩니다.
- (추후 API 문서 호스팅 링크 기입 예정)
