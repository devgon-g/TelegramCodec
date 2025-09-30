# TelegramCodec

## 프로젝트 소개
TelegramCodec은 텔레그램(고정 길이 문자열 전문) 메시지를 애노테이션으로 정의하고, 런타임에 리플렉션을 활용해 문자열을 생성·파싱하는 라이브러리입니다. 현재 모든 핵심 구현은 `pe.devgon` 패키지 아래로 통합되어 있으며, 전문 인코딩/디코딩 파이프라인과 TCP 템플릿 클라이언트를 제공합니다.

## 주요 기능
- `@Protocol`, `@FieldItem`, `@CompositeItem`, `@ListItem`, `@Filler` 애노테이션으로 전문 레이아웃 선언
- `pe.devgon.telegram.encode.EncodeItemHandlers` / `pe.devgon.telegram.decode.DecodeItemHandlers`를 통한 반사 기반 문자열 조립·파싱
- `StringTelegramRequestSource`, `StringTelegramResponseSource`로 고정 길이 문자열 버퍼 관리
- `TelegramTemplate` + `TcpClient` 조합으로 TCP 전문 송수신 템플릿 제공
- `Pad`, `CastTo`, `CountHolder`, `Masking`, `IDGenerator` 등 공용 유틸리티 제공

## 기술 스택 및 의존성
- Java 8
- Apache Maven 3.x
- Lombok (annotation processing 활성화 필요)
- Apache Commons Lang3
- SLF4J & Logback
- 일부 유틸(`IdLoginTokens`)은 `cj.tlj.*` 사내 라이브러리를 기대하므로, 실제 빌드 시 해당 모듈을 추가하거나 대체 구현이 필요합니다.

## 디렉터리 구조
```
src/
  main/
    java/
      pe/devgon/
        functional/           # Pad, TypeCast, CastTo 등 함수형 유틸
        logging/              # IfLog, IfLogTemplate
        telegram/             # 템플릿, TCP 클라이언트, 인코더/디코더, 애노테이션
        util/                 # 날짜/ID 생성, 마스킹, 시퀀스 유틸
    resources/
      logback.xml             # 기본 로그 설정
  test/
    java/pe/devgon/functional # Pad, TypeCast 등의 단위 테스트
```

## 핵심 패키지 개요
- `pe.devgon.telegram.annotation` : 전문 설계를 위한 애노테이션 모음
- `pe.devgon.telegram.encode` : 필드 값을 문자열로 직렬화하는 핸들러 팩토리
- `pe.devgon.telegram.decode` : 응답 문자열을 객체로 역직렬화하는 핸들러
- `pe.devgon.telegram` : `TelegramTemplate`, `TcpClient`, `SimpleTcpClient`, `CountHolder`
- `pe.devgon.functional.util` : 패딩/형 변환 관련 도구
- `pe.devgon.util` : ID/날짜 생성, 마스킹, XSS 이스케이프 등 보조 기능

## 사용 방법
### 1. 전문 클래스 정의
```java
import pe.devgon.telegram.annotation.*;
import pe.devgon.functional.util.Pad;

@Protocol(itemCount = 5, fillers = {
    @Filler(seq = 4, size = 10)
})
public class SampleRequest {
    @FieldItem(seq = 0, size = 4, pad = Pad.LEFT_ZERO)
    private int length;

    @FieldItem(seq = 1, size = 10)
    private String userId;

    @CompositeItem(seq = 2)
    private Detail detail;

    @ListItem(countSeq = 3, countSize = 2, countPad = Pad.LEFT_ZERO, dataSeq = 4)
    private List<LineItem> items;
}

@Protocol(itemCount = 2)
class Detail {
    @FieldItem(seq = 0, size = 8)
    private String requestDate;

    @FieldItem(seq = 1, size = 15, pad = Pad.RIGHT_SPACE)
    private String comment;
}
```
`@ListItem`는 카운트 필드(`countSeq`)와 실제 데이터 필드(`dataSeq`)를 별도의 시퀀스로 배치하며, `fixedCount`를 지정하면 응답 본문에서 개수를 읽지 않고 고정 값으로 처리합니다.

### 2. 전문 생성 및 송신
```java
TcpClient client = new SimpleTcpClient("core-system", "127.0.0.1", 9000);
TelegramTemplate template = new TelegramTemplate(client);

SampleRequest request = new SampleRequest();
// TODO: 필드 값 세팅

SampleResponse response = template.submit(request, SampleResponse.class);
```
`submit`은 내부적으로 `EncodeItemHandlers`로 요청 전문을 빌드하고, `TcpClient`를 통해 전송한 뒤, `DecodeItemHandlers`로 응답 객체를 채워 반환합니다. `SimpleTcpClient`는 `Socket` 기반 구현 예시이며, 운영 환경에 맞춰 `TcpClient`를 확장해 사용할 수 있습니다.

### 3. 응답 파싱
```java
@Protocol(itemCount = 3)
public class SampleResponse {
    @FieldItem(seq = 0, size = 4)
    private String responseCode;

    @FieldItem(seq = 1, size = 20)
    private String message;

    @CompositeItem(seq = 2)
    private Detail detail;
}
```
응답 전문에서도 동일한 애노테이션을 사용하며, `DecodeItemHandlers`가 시퀀스 순서대로 문자열을 잘라 필드에 주입합니다.

## 빌드 및 테스트
```bash
mvn clean test
```
- Lombok을 사용하므로 IDE에서 annotation processing을 활성화해야 경고 없이 컴파일됩니다.
- `cj.tlj.*` 참조가 있는 클래스는 해당 라이브러리가 없으면 컴파일 오류가 발생하므로, 필요 시 의존성을 추가하거나 import를 제거해야 합니다.

## 로깅
- `src/main/resources/logback.xml`에서 콘솔과 `/tmp/access.log`(daily rolling)로 로그를 출력하도록 설정돼 있습니다.
- 패키지별 로깅 레벨은 필요에 따라 조정하세요.

## 참고 및 주의 사항
- 모든 구현이 `pe.devgon` 패키지로 통합되었으므로, 외부에서 가져온 예제 코드를 사용할 때 패키지 경로를 확인하세요.
- `IdLoginTokens` 등 일부 클래스는 사내 시스템(`cj.tlj.*`)을 가정하고 있어 외부 환경에서는 대체 구현이나 주석 처리가 필요합니다.
- 고정 길이 전문 특성상 필드 길이와 패딩 정책을 정확히 맞춰야 하며, `valueVerifier`(디폴트 구현)는 길이가 초과되면 잘라내므로 데이터 손실 가능성을 염두에 두고 설계해야 합니다.
