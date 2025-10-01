# TelegramCodec

## 프로젝트 개요
TelegramCodec은 고정 길이 문자열로 구성된 텔레그램 전문을 어노테이션 기반으로 정의하고, 객체 ↔ 문자열 간에 직렬화/역직렬화를 수행하는 Java 8 라이브러리입니다. 템플릿/클라이언트 조합을 통해 TCP 통신까지 처리할 수 있으며, 인터페이스 로그(IfLog)를 표준 포맷으로 남기는 기능도 제공합니다.

## 주요 기능
- `@Protocol`, `@FieldItem`, `@CompositeItem`, `@ListItem`, `@Filler` 등 도메인 모델을 전문 레이아웃에 매핑하는 어노테이션
- `EncodeItemHandlers`/`DecodeItemHandlers`를 이용한 반사(reflection) 기반 전문 빌드 및 파싱
- `StringTelegramRequestSource`, `StringTelegramResponseSource`를 통한 고정 길이 버퍼 처리
- `TelegramTemplate` + `TcpClient` 조합으로 전문 송수신 흐름 일원화 (`SimpleTcpClient` 제공)
- `Pad`, `CastTo`, `Masking`, `IDGenerator`, `RollingSequenceIdGenerator` 등 재사용 가능한 유틸리티 집합
- `IfLogTemplate`를 이용한 전문 입출 로그(타깃 시스템, IN/OUT, 전문 본문 등) 출력

## 패키지 구조
```
src/main/java/pe/devgon
├─ telegram/annotation   # 전문 정의용 어노테이션 모음
├─ telegram/encode       # 전문 빌더, 요청 소스(StringTelegramRequestSource 등)
├─ telegram/decode       # 전문 파서, 응답 소스(StringTelegramResponseSource 등)
├─ telegram              # 템플릿, TCP 클라이언트, 카운트 홀더
├─ functional/lang       # TypeCast, CastTo, FieldGetter 유틸
├─ functional/util       # Pad 등 부가 유틸리티
├─ util                  # 날짜/ID/마스킹/XSS/시퀀스 유틸 모음
└─ logging               # IfLog 모델 및 로깅 템플릿
```
테스트는 `src/test/java/pe/devgon/functional` 아래에 위치하며 Pad와 TypeCast 동작을 검증합니다.

## 사용 예시
1. **전문 클래스 작성**: POJO 필드를 어노테이션으로 꾸며 전문 구조를 정의합니다.
   ```java
   @Protocol(itemCount = 3)
   public class SampleRequest {
       @FieldItem(seq = 0, size = 4, pad = Pad.LEFT_ZERO)
       private int length;

       @FieldItem(seq = 1, size = 10)
       private String userId;

       @CompositeItem(seq = 2)
       private Detail detail;
   }
   ```
2. **송신 실행**: `TelegramTemplate`이 객체를 문자열 전문으로 만들고, `TcpClient` 구현이 전송합니다.
   ```java
   TcpClient client = new SimpleTcpClient("core-system", "127.0.0.1", 9000);
   TelegramTemplate template = new TelegramTemplate(client);
   SampleResponse response = template.submit(request, SampleResponse.class);
   ```
3. **응답 파싱**: `DecodeItemHandlers`가 응답 문자열을 `SampleResponse` 객체에 주입합니다. 리스트 필드는 `@ListItem`의 `countSeq`/`dataSeq`에 따라 카운트와 데이터를 분리해 처리합니다.

## 빌드 & 테스트
```bash
C:\temp\apache-maven-3.9.6\bin\mvn.cmd -f pom.xml clean test
```
- `pom.xml`에서 Maven 인코딩을 UTF-8로 고정했으므로 별도 VM 옵션 없이 빌드가 가능합니다.
- Windows 기본 인코딩이 MS949라도 한글 주석이 깨지지 않습니다.
- Lombok을 사용하므로 IDE에서 annotation processing을 활성화해야 합니다.

## 구성 요소 상세
- **TelegramTemplate**: 요청 객체를 전문 문자열로 빌드하고 TCP 전송 후 응답 객체를 생성합니다. 처리 결과를 `IfLogTemplate`을 통해 기록합니다.
- **SimpleTcpClient**: `Socket` 기반 기본 구현으로, 요청 전문을 전송하고 전체 응답 바이트를 읽어 `StringTelegramResponseSource`로 래핑합니다.
- **EncodeItemHandlers / DecodeItemHandlers**: Reflection을 이용해 필드 순서(`seq`)와 길이(`size`)에 맞춰 전문을 조립/해석합니다. 리스트 항목은 `CountHolder`로 반복 횟수를 공유합니다.
- **StringTelegramRequestSource / ResponseSource**: 고정 길이 전문 버퍼 역할을 수행하며, 응답 쪽은 내부적으로 `ByteBuffer`와 `Charset`을 사용합니다.
- **functional.lang / util**: 문자열 패딩(`Pad`), 타입 변환(`CastTo`, `TypeCast`), 일시/ID 생성, 마스킹, 연속번호 생성 등의 보조 로직을 제공합니다.
- **logging**: `IfLog` 모델과 `IfLogTemplate` 유틸리티가 전문 입출 로그를 일관된 포맷으로 남깁니다.
- **테스트**: JUnit Jupiter 5.9.3 기반으로 `EncodeItemHandlers`, `DecodeItemHandlers`, `StringTelegramResponseSource`, `TelegramTemplate`를 검증하는 단위 테스트를 제공합니다.

## 주의 사항
- Maven 빌드 인코딩을 UTF-8로 고정했으므로 소스를 다른 인코딩으로 저장하면 경고가 발생할 수 있습니다.
- 테스트용 DTO에서 필드 접근자(`public` 혹은 getter/setter)가 없으면 리플렉션 기반 핸들러가 값을 세팅하지 못하니 주의하세요.
- 일부 주석이 기존 인코딩 문제로 깨져 있을 수 있습니다. 가능하면 소스 파일 전체를 UTF-8로 정렬하세요.
- `IdLoginTokens`와 같이 외부(`cj.tlj.*`) 의존이 필요했던 이전 코드가 제거되었으므로, 추가로 필요한 내부 라이브러리가 있다면 직접 구현하거나 의존성을 명확히 해야 합니다.
- TCP 통신 시 운영 환경에 맞는 타임아웃/예외 처리가 필요하다면 `TcpClient` 인터페이스를 확장해 사용하세요.
