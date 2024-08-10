function goToSignupPage() {
    window.location.href = '/domain/join';
}

document.addEventListener('DOMContentLoaded', function () {
    const signupButton = document.querySelector('#signupButton');
    if (signupButton) {
        signupButton.addEventListener('click', goToSignupPage);
    }
});