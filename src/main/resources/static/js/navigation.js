function goToSignupPage() {
    window.location.href = '/templates/domain/main.html';
}

document.addEventListener('DOMContentLoaded', function () {
    const signupButton = document.querySelector('#signupButton');
    if (signupButton) {
        signupButton.addEventListener('click', goToSignupPage);
    }
});