# handler의 메소드 별로 dto를 만들어야 할까요? 공통 속성을 가진 dto들이 많습니다. 이럴 경우 효과적인 방법이 있을까요?

메소드 별로 DTO를 만들어야 할지 여부는 핸들러 메소드의 요청과 응답 데이터의 복잡성, 공통 속성, 그리고 개별 속성에 따라 달라집니다. 공통 속성을 가진 DTO들이 많은 경우에는 다음과 같은 방법으로 효과적으로 처리할 수 있습니다.

* 상속: 공통 속성을 가진 기본 클래스를 만들고, 각 메소드에 대한 DTO 클래스에서 이 클래스를 상속받을 수 있습니다. 이렇게 하면 공통 속성을 재사용할 수 있으며, 개별 메소드에서 필요한 추가 속성을 정의할 수 있습니다.

예를 들어:
```kotlin
// 공통 속성을 가진 기본 클래스
open class BaseUserDto(val name: String, val email: String)

// 메소드별 DTO 클래스
data class CreateUserRequest(val password: String) : BaseUserDto(name, email)
data class UpdateUserRequest(val id: Long) : BaseUserDto(name, email)
```

* 컴포지션: 공통 속성을 가진 별도의 클래스를 만들고, 각 메소드에 대한 DTO 클래스에서 이 클래스를 포함시킬 수 있습니다. 이렇게 하면 공통 속성을 재사용하면서 각 메소드에 필요한 추가 속성을 정의할 수 있습니다.

예를 들어:
```kotlin
// 공통 속성을 가진 별도의 클래스
data class UserCommonAttributes(val name: String, val email: String)

// 메소드별 DTO 클래스
data class CreateUserRequest(val commonAttributes: UserCommonAttributes, val password: String)
data class UpdateUserRequest(val commonAttributes: UserCommonAttributes, val id: Long)
```

상황에 따라 상속이나 컴포지션 중 적절한 방법을 선택하여 공통 속성을 재사용하고, 코드의 중복을 줄일 수 있습니다. 각 방법은 장단점이 있으며, 프로젝트의 요구 사항과 개발 팀의 취향에 따라 선택해야 합니다.
