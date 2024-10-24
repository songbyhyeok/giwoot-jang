const idDuplicateVerification = (msgList, isAvailableResource) => {
    const id = document.getElementById('id');
    const dupChkBtn = document.getElementById('duplicateCheckBtn');
    let trustedId = '';

    const isValidId = (idValue) => {
        return idValue.length >= 4;
    };

    if (id && dupChkBtn) {
        if (id.validationMessage === '') {
            id.setCustomValidity(msgList.DUPLICATE_CHECK);
        }

        id.addEventListener('input', () => {
            if (id.validationMessage === msgList.AVAILABLE_ID && id.value !== trustedId) {
                id.setCustomValidity(msgList.DUPLICATE_CHECK);
            }
        })

        dupChkBtn.addEventListener('click', async () => {
            const idValue = id.value.trim();
            if (!isValidId(idValue)) {
                id.setCustomValidity(msgList.INVALID_ID);
                id.reportValidity();
                return;
            }

            dupChkBtn.disabled = true;
            try {
                const isAvailableRes = await isAvailableResource('/signup/verification/ids/', idValue);
                id.setCustomValidity(!isAvailableRes ? msgList.DUPLICATE : msgList.AVAILABLE_ID);
                id.reportValidity();
            } catch (error) {
                console.error("ID 중복 확인 중 오류 발생:", error);
                alert("서버 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            } finally {
                dupChkBtn.disabled = false;
            }
        });
    }

    return id;
};

const passwordVerification = (msgList) => {
    const pwd = document.getElementById('password');
    const pwdConfirm = document.getElementById('passwordConfirm');

    const isPasswordStrong = (pwd) => {
        // 긍정적 전방 탐색(positive lookahead)
        // .* 어떤 문자(.)든 0개 이상(*), 현재 위치 이후 어딘가에 숫자가 있는지 확인
        // 대.소문자 중 하나 이상 포함되어야 하며, 숫자 0 ~ 9 사이의 숫자가 하나 이상 포함되어야 하고, 특수문자가 하나 이상 포함되어 총 8글자 이상이어야 한다.
        //const strongPasswordRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?](?=.{8,}))$/;
        const strongPasswordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?])[A-Za-z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]{8,}$/;
        return strongPasswordRegex.test(pwd);
    };

    if (pwd && pwdConfirm) {
        pwd.setCustomValidity(msgList.WEAK);
        pwdConfirm.setCustomValidity(msgList.MISMATCH);

        pwd.addEventListener('input', () => {
            pwd.setCustomValidity(!isPasswordStrong(pwd.value) ? msgList.WEAK : msgList.SUFFICIENT);
            pwd.reportValidity();
        });

        pwdConfirm.addEventListener('input', () => {
            pwdConfirm.setCustomValidity(pwd.value !== pwdConfirm.value ? msgList.MISMATCH : msgList.MATCH);
            pwdConfirm.reportValidity();
        });

        return {
            pwd: pwd,
            pwdConfirm: pwdConfirm
        };
    }
}

const phoneAuthReq = (msgList, isNumberValid, isAvailableResource) => {
    const mPhoneN = document.getElementById('mPhoneN');
    const bPhoneN = document.getElementById('bPhoneN');
    const verBtn = document.getElementById('VerificationCodeBtn');
    const verDiv = document.getElementById('verificationCodeDiv');

    if (verBtn && mPhoneN && bPhoneN && verDiv) {
        mPhoneN.setCustomValidity(msgList.INVALID_NUMBER);
        bPhoneN.setCustomValidity(msgList.INVALID_NUMBER);

        mPhoneN.addEventListener('input', () => {
            isNumberValid(mPhoneN);
            // [^...]: 대괄호 안의 문자 외의 모든 문자와 일치합니다.
            // g 플래그: 글로벌 검색을 의미하며, 문자열 전체에서 해당 패턴을 모두 찾습니다.
            // 숫자 외 모든 코드를 제거
            mPhoneN.value = mPhoneN.value.replace(/[^\d]/g, '').slice(0, 4);
        })

        bPhoneN.addEventListener('input', () => {
            isNumberValid(bPhoneN);
            bPhoneN.value = bPhoneN.value.replace(/[^\d]/g, '').slice(0, 4);
        })

        verBtn.addEventListener('click', async() => {
            const isMPhoneValid = isNumberValid(mPhoneN);
            const isBPhoneValid = isNumberValid(bPhoneN);
            const combinedPhone = '010' + mPhoneN.value + bPhoneN.value;

            verBtn.disabled = true;
            try {
                const authNumber = await isAvailableResource('/signup/verification/phones/', combinedPhone);

            } catch (error) {
                console.error("인증번호 요청 중 오류 발생:", error);
                alert("서버 오류가 발생했습니다. 나중에 다시 시도해주세요.");
            } finally {
                verBtn.disabled = false;
            }

            verDiv.style.display = isMPhoneValid && isBPhoneValid ? '' : 'none';
        });
    }
};

const authCodeVerification = (msgList, isNumberValid) => {
    const authNumber = document.getElementById('authNumber');
    const retransmissionBtn = document.getElementById('retransmissionBtn');
    const authBtn = document.getElementById('authBtn');

    if (authNumber && retransmissionBtn && authBtn) {
        authNumber.setCustomValidity(msgList.INVALID_NUMBER);

        authNumber.addEventListener('input', () => {
            if (isNumberValid(authNumber)) {
                authNumber.setCustomValidity(msgList.AVAILABLE_NUMBER);
            }

            authNumber.value = authNumber.value.replace(/[^\d]/g, '').slice(0, 4);
        })

        retransmissionBtn.addEventListener('click', () => {
            if (authNumber.validationMessage === msgList.AVAILABLE_NUMBER) {

            }
        })

        authBtn.addEventListener('click',() => {
            if (authNumber.validationMessage === msgList.AVAILABLE_NUMBER) {
            }
        })
    }
}

const signupValidator = () => {
    const signupForm = document.getElementById('signupForm');
    const NOTIFICATION_MESSAGES = {
        AVAILABLE_ID: "사용 가능한 아이디입니다.",
        INVALID_ID: "유효하지 않은 아이디입니다. 최소 4자 이상이어야 합니다.",
        DUPLICATE_CHECK: "중복 검사를 누르십시오.",
        DUPLICATE: "중복된 아이디입니다.",
        SUFFICIENT: '비밀번호 사용이 가능합니다.',

        WEAK: '비밀번호는 최소 8자 이상이며, 대.소문자, 숫자, 특수기호를 포함해야 합니다.',
        MATCH: '비밀번호가 일치합니다.',
        MISMATCH: '비밀번호가 일치하지 않습니다.',
        INVALID_NUMBER: "4자리 숫자로 구성되어야 합니다.",
        AVAILABLE_NUMBER: "유효한 번호입니다."
    };

    const isAvailableResource = async (url, res) => {
        console.log(url + res);
        const response = await axios.get(`${url}${res}`);
        return !response.data;
    };

    const isNumberValid = (input) => {
        // 4자리 이상의 숫자가 반복되는
        if (input.value.length < 4 || !/^\d+$/.test(input.value)) {
            input.setCustomValidity(NOTIFICATION_MESSAGES.INVALID_NUMBER);
            input.reportValidity();
            return false;
        }

        input.setCustomValidity('');
        return true;
    };

    const processedId = idDuplicateVerification(NOTIFICATION_MESSAGES, isAvailableResource);
    const processedPwdInfo = passwordVerification(NOTIFICATION_MESSAGES);
    phoneAuthReq(NOTIFICATION_MESSAGES, isNumberValid, isAvailableResource);
    authCodeVerification(NOTIFICATION_MESSAGES, isNumberValid);

    signupForm.addEventListener('click', (event) => {
        if (processedId.validationMessage === NOTIFICATION_MESSAGES.AVAILABLE_ID) {
            processedId.setCustomValidity('');
        }
        if (processedPwdInfo.pwd.validationMessage === NOTIFICATION_MESSAGES.SUFFICIENT) {
            processedPwdInfo.pwd.setCustomValidity('');
        }
        if (processedPwdInfo.pwdConfirm.validationMessage === NOTIFICATION_MESSAGES.MATCH) {
            processedPwdInfo.pwdConfirm.setCustomValidity('');
        }
    })
}

document.addEventListener('DOMContentLoaded', signupValidator);