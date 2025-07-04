package sp.spb.auth.kakao_authentication.controller.request_form;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccessToeknRequestForm {
    String code;
}
