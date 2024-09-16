const validatePassword = () => {
    const form = document.getElementById('signupForm');
    const password = document.getElementById('password');
    const passwordConfirm = document.getElementById('passwordConfirm');

    function validatePasswordMatch() {
        if (password.value.length <= passwordConfirm.value.length && password.value !== passwordConfirm.value) {
            passwordConfirm.setCustomValidity('비밀번호가 일치하지 않습니다.');
            passwordConfirm.value = '';
        } else {
            passwordConfirm.setCustomValidity('');
        }
        passwordConfirm.reportValidity(); // 유효성 검사 메시지를 브라우저에 반영합니다.
    }

    // 비밀번호와 비밀번호 확인 필드에서 입력이 있을 때마다 유효성 검사 수행
    password.addEventListener('input', validatePasswordMatch);
    passwordConfirm.addEventListener('input', validatePasswordMatch);

    form.addEventListener('submit', function (event) {
        validatePasswordMatch();

        // 유효성 검사에 실패한 경우 폼 제출 방지
        if (password.value !== passwordConfirm.value) {
            event.preventDefault(); // 폼 제출을 방지합니다.
            passwordConfirm.focus(); // 사용자에게 오류를 바로 보여줍니다.
        }
    });
}

document.addEventListener('DOMContentLoaded', validatePassword);
