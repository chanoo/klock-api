# Date, LocalDateTime, LocalDate 차이는 무엇입니까?

LocalDateTime과 LocalDate는 Java 8에서 도입된 java.time 패키지의 일부입니다. 이 패키지는 기존의 java.util.Date 및 java.util.Calendar 클래스의 문제를
해결하기 위해 만들어졌습니다. java.util.Date는 불변 객체가 아니며, 가독성이 떨어지고 오류가 발생하기 쉬운 API를 가지고 있습니다.

LocalDateTime을 사용하는 이유는 다음과 같습니다.

1. 불변 객체: LocalDateTime은 불변 객체이므로, 쓰레드 안전하고 예기치 않은 변경으로부터 안전합니다.
2. 명확한 API: LocalDateTime은 명확하고 직관적인 메서드를 제공합니다.
3. 구역 정보가 없음: LocalDateTime은 시간대 정보를 포함하지 않으므로, 저장되는 시간 정보가 현지 시간임을 확실하게 할 수 있습니다.

LocalDateTime과 LocalDate의 차이는 다음과 같습니다.

* LocalDateTime: 날짜와 시간 정보를 모두 포함합니다. 예: 2023-03-16T12:34:56
* LocalDate: 오직 날짜 정보만을 포함하며, 시간 정보는 포함하지 않습니다. 예: 2023-03-16

따라서 LocalDateTime은 일시 정보를 다룰 때 사용하며, LocalDate는 날짜 정보만 필요한 경우 사용합니다.
