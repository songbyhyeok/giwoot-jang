// 중앙화된 URL 관리를 위한 객체
const routes = {
    login: '/login',
    signup: '/signup'
};

// 이벤트 리스너 설정 함수
const connectNavigation = () => {
    // 로그인 버튼 클릭 시 로그인 페이지로 이동
    const loginButton = document.getElementById('loginButton');
    if (loginButton) {
        loginButton.addEventListener('click', () => {
            window.location.href = routes.login;
        });
    }

    // 회원가입 버튼 클릭 시 회원가입 페이지로 이동
    const signupButton = document.getElementById('signupButton');
    if (signupButton) {
        signupButton.addEventListener('click', () => {
            window.location.href = routes.signup;
        });
    }
};

document.addEventListener('DOMContentLoaded', connectNavigation);
