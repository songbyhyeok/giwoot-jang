const Timer = (() => {
    const timer = document.getElementById("clock");

    let time; // 현재 시간
    let intervalID; // setInterval ID
    let initialTime = 3 * 60 * 1000; // 초기 시간 (3분)
    let onCompleteCallback = null; // 타이머 종료 시 호출할 콜백 함수

    // 타이머 설정 함수 (시간과 콜백 함수 설정)
    const set = (duration, callback) => {
        initialTime = duration * 7000; // 시간을 밀리초 단위로 변환
        time = initialTime; // 현재 시간 설정
        if (typeof callback === 'function') {
            // 콜백 함수를 타이머 종료 시에만 실행하도록 설정
            onCompleteCallback = callback;
        }
    };

    // 카운트다운 함수
    const countDown = () => {
        const minutes = Math.floor(time / (1000 * 60));
        const seconds = Math.floor((time % (1000 * 60)) / 1000);

        // 타이머를 표시하는 HTML 요소
        if (timer) {
            timer.innerHTML = `${minutes}분 ${seconds}초`;
        }

        // 시간이 다 되었으면 타이머 종료
        if (time <= 0) {
            clearInterval(intervalID); // 타이머 종료
            if (onCompleteCallback) {
                onCompleteCallback();
            }
        }

        // 남은 시간을 1초씩 차감
        time -= 1000;
    };

    // 타이머 초기화 함수
    const resetTimer = () => {
        clearInterval(intervalID); // 기존 타이머 종료
        time = initialTime; // 시간을 초기값으로 리셋
        countDown(); // 초기화 시 바로 카운트다운 표시
        intervalID = setInterval(countDown, 1000); // 다시 1초마다 카운트다운 시작
    };

    // 타이머 시작 함수
    const startTimer = () => {
        if (intervalID) {
            clearInterval(intervalID); // 기존 타이머가 있을 경우 정지
        }

        time = initialTime; // 타이머 시작 시 초기화
        intervalID = setInterval(countDown, 1000); // 1초마다 카운트다운 시작
    };

    // 타이머 멈추는 함수
    const stopTimer = () => {
        clearInterval(intervalID); // 타이머 멈춤
        timer.innerHTML = '';
    };

    // 타이머 종료 시 호출할 콜백 함수 설정
    const onComplete = (callback) => {
        if (typeof callback === 'function') {
            onCompleteCallback = async () => {
                await callback(); // 비동기 함수 호출을 기다림
            };
        }
    };

    // 외부에서 사용할 수 있는 메서드 반환
    return {
        set: set,
        start: startTimer,
        reset: resetTimer,
        stop: stopTimer,
    };
})();

const handleResourceAsync = async (method, url, res) => {
    try {
        console.log(method + url);

        //html meta에서 csrf token 가져오기
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const jsonHeaders = {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken,
        };

        console.log(jsonHeaders)

        let response;
        switch (method.toUpperCase()) {
            case 'GET':
                response = await axios.get(`${url}${res}`);
                break;
            case 'POST':
                response = await axios.post(url,
                    res, {headers: jsonHeaders});
                break;
            case 'PUT':
                response = await axios.put(url, res);
                break;
            case 'DELETE':
                response = await axios.delete(`${url}${res}`);
                break;
            default:
                throw new Error('Invalid HTTP method: ' + method);
        }

        console.log(response.data)
        return response.data;
    } catch (error) {
        console.error('Error making request:', error);
        throw error;
    }
};

const handleAsyncOperation = async (asyncActionBtn, method, url, res, callback = null) => {
    asyncActionBtn.disabled = true;  // 버튼 비활성화
    let response = false;

    try {
        response = await handleResourceAsync(method, url, res);
        console.log("인증번호 요청 성공");
    } catch (error) {
        console.error("인증번호 요청 중 오류 발생:", error);  // 오류 발생 시 로그 출력
        alert("서버 오류가 발생했습니다. 나중에 다시 시도해주세요.");  // 사용자에게 오류 알림
    } finally {
        asyncActionBtn.disabled = false;  // 버튼 활성화
        if (callback)
            callback();
    }

    return response;
};

const validatePhoneNumber = (inputField, inputLen, validMessage, invalidMessage) => {
    const isNumberValid = (numberLen, number) => {
        const regex = new RegExp(`^[0-9]{${numberLen}}$`);  // 정규 표현식 객체로 변경, 인자값을 넣었기 때문
        return regex.test(number);
    };

    inputField.addEventListener('input', () => {
        if (!isNumberValid(inputLen, inputField.value)) {
            inputField.setCustomValidity(inputLen + invalidMessage);
        } else {
            inputField.setCustomValidity(validMessage);
        }

        inputField.value = inputField.value.replace(/[^\d]/g, '').slice(0, inputLen);
        inputField.reportValidity();
    })
}

const validateName = (msgList) => {
    const name = document.getElementById('name');

    // -> ^ : 문자열의 시작을 나타냅니다.
    // -> [가-힣] : 한글 음절(초성, 중성, 종성) 범위만 허용합니다.
    // -> {2,} : 2글자 이상을 의미합니다.
    // -> $ : 문자열의 끝을 나타냅니다.
    // 한글 이름만 작성되고, 2글자 이상, 공백을 포함할 수 없다.
    const isValidName = (name) => {
        const regex = /^[가-힣]{2,}$/;
        return regex.test(name);
    };

    if (name) {
        if (name.value.trim() === '') {
            name.setCustomValidity(msgList.INVALID_NAME);
        }

        name.addEventListener('input', () => {
            if (!isValidName(name.value)) {
                name.setCustomValidity(msgList.INVALID_NAME);
            } else {
                name.setCustomValidity(msgList.AVAILABLE_NAME);
            }

            name.reportValidity();
        });
    }

    return name;
}

const validateDuplicateId = (msgList, handleAsyncOperation) => {
    const id = document.getElementById('id');
    const dupChkBtn = document.getElementById('duplicate-check-btn');

    const isValidId = (idValue) => {
        return idValue.length >= 4;
    };

    if (id && dupChkBtn) {
        if (id.value.trim() === '') {
            id.setCustomValidity(msgList.INVALID_ID);
        }

        id.addEventListener('input', () => {
            if (!isValidId(id.value.trim())) {
                id.setCustomValidity(msgList.INVALID_ID);
            } else if (id.validationMessage !== msgList.AVAILABLE_ID) {
                id.setCustomValidity(msgList.DUPLICATE_CHECK);
            }

            id.reportValidity();
        });

        dupChkBtn.addEventListener('click', async () => {
            const idValue = id.value.trim();
            if (!isValidId(idValue)) {
                id.setCustomValidity(msgList.INVALID_ID);
                id.reportValidity();
                return;
            }

            const isRegisteredId = await handleAsyncOperation(dupChkBtn, 'GET', '/signup/verification/ids/', idValue)
            id.setCustomValidity(isRegisteredId ? msgList.DUPLICATE : msgList.AVAILABLE_ID);
            id.reportValidity();
        });
    }

    return id;
};

const validatePassword = (msgList) => {
    const pwd = document.getElementById('password');
    const pwdConfirm = document.getElementById('password-confirm');
    const pwdStatus = document.getElementById('pwd-status');
    const pwdConfirmStatus = document.getElementById('pwd-confirm-status');

    const isPasswordStrong = (pwd) => {
        // 긍정적 전방 탐색(positive lookahead)
        // .* 어떤 문자(.)든 0개 이상(*), 현재 위치 이후 어딘가에 숫자가 있는지 확인
        // 대.소문자 중 하나 이상 포함되어야 하며, 숫자 0 ~ 9 사이의 숫자가 하나 이상 포함되어야 하고, 특수문자가 하나 이상 포함되어 총 8글자 이상이어야 한다.
        //const strongPasswordRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?](?=.{8,}))$/;
        const strongPasswordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?])[A-Za-z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]{8,}$/;
        return strongPasswordRegex.test(pwd);
    };

    const togglePwdConfirmStatus = () => {
        if (pwd.value !== pwdConfirm.value) {
            pwdConfirm.setCustomValidity(msgList.MISMATCH);

            pwdConfirmStatus.textContent = msgList.MISMATCH;
            pwdConfirmStatus.style.color = 'red';
        } else {
            pwdConfirm.setCustomValidity(msgList.MATCH);

            pwdConfirmStatus.textContent = msgList.MATCH;
            pwdConfirmStatus.style.color = 'green';
        }
    }

    if (pwd && pwdConfirm) {
        if (pwd.value.trim() === '') {
            pwd.setCustomValidity(msgList.WEAK);
        }

        if (pwdConfirm.value.trim() === '') {
            pwdConfirm.setCustomValidity(msgList.MISMATCH);
        }

        pwd.addEventListener('input', () => {
            if (!isPasswordStrong(pwd.value)) {
                pwd.setCustomValidity(msgList.WEAK);
                pwdStatus.textContent = msgList.WEAK;
                pwdStatus.style.color = 'red';
            } else {
                pwd.setCustomValidity(msgList.SUFFICIENT);
                pwdStatus.textContent = msgList.SUFFICIENT;
                pwdStatus.style.color = 'green';
            }

            pwd.reportValidity();
            togglePwdConfirmStatus();
        });

        pwdConfirm.addEventListener('input', () => {
            togglePwdConfirmStatus();
        });

        return {
            pwd: pwd,
            pwdConfirm: pwdConfirm,
            pwdStatus: pwdStatus,
            pwdConfirmStatus: pwdConfirmStatus,
        };
    }
}

const sendVerificationCode = (msgList, handleAsyncOperation) => {
    const name = document.getElementById('name');
    const fPhoneN = document.getElementById('front-phone-number');
    const mPhoneN = document.getElementById('mid-phone-number');
    const bPhoneN = document.getElementById('back-phone-number');
    const authCodeGenerationBtn = document.getElementById('auth-code-generation-btn');
    const authCodeDiv = document.getElementById('auth-code-div');
    const numberInputField = document.getElementById('auth-number-input');

    if (name && fPhoneN && mPhoneN && bPhoneN &&
        authCodeGenerationBtn && authCodeDiv) {
        if (fPhoneN.value.trim() === '') {
            fPhoneN.setCustomValidity(3 + msgList.INVALID_NUMBER);
        } else if (fPhoneN.value.length === 3) {
            fPhoneN.setCustomValidity(msgList.AVAILABLE_NUMBER);
        }
        if (mPhoneN.value.trim() === '') {
            mPhoneN.setCustomValidity(4 + msgList.INVALID_NUMBER);
        }
        if (bPhoneN.value.trim() === '') {
            bPhoneN.setCustomValidity(4 + msgList.INVALID_NUMBER);
        }

        fPhoneN.addEventListener('input', () => {
            validatePhoneNumber(fPhoneN, 3, msgList.AVAILABLE_NUMBER, msgList.INVALID_NUMBER)
        })
        mPhoneN.addEventListener('input', () => {
            validatePhoneNumber(mPhoneN, 4, msgList.AVAILABLE_NUMBER, msgList.INVALID_NUMBER)
        })
        bPhoneN.addEventListener('input', () => {
            validatePhoneNumber(bPhoneN, 4, msgList.AVAILABLE_NUMBER, msgList.INVALID_NUMBER)
        })

        authCodeGenerationBtn.addEventListener('click', async () => {
            if (fPhoneN.value.length === 3 &&
                mPhoneN.value.length === 4 &&
                bPhoneN.value.length === 4) {
                try {
                    const smsAuthRequest = {
                        name: name.value,
                        fPhoneN: fPhoneN.value,
                        mPhoneN: mPhoneN.value,
                        bPhoneN: bPhoneN.value,
                    };

                    const response = await handleAsyncOperation(authCodeGenerationBtn, 'POST', '/signup/verification/phones/auth', smsAuthRequest);
                    if (response) {
                        authCodeGenerationBtn.disabled = true;
                        authCodeGenerationBtn.innerText = '재전송';

                        authCodeDiv.style.display = '';
                        fPhoneN.setCustomValidity(msgList.DISPATCHED_SUCCESSFULLY);
                        numberInputField.setCustomValidity(msgList.AUTH_VERIFICATION_REQUIRED);

                        Timer.set(1, async () => {
                            await handleAsyncOperation(authCodeGenerationBtn,
                                'POST', '/signup/verification/phones/clear', smsAuthRequest);
                        });

                        Timer.start();
                    }
                } catch (error) {
                    console.error("비동기 작업 중 오류 발생:", error);
                }
            }
        });
    }

    return {
        fPhoneN: fPhoneN,
        mPhoneN: mPhoneN,
        bPhoneN: bPhoneN,
        authCodeGenerationBtn: authCodeGenerationBtn,
    };
};

const verifyAuthCode = (msgList, handleAsyncOperation) => {
    const name = document.getElementById('name');
    const fPhoneN = document.getElementById('front-phone-number');
    const mPhoneN = document.getElementById('mid-phone-number');
    const bPhoneN = document.getElementById('back-phone-number');
    const authCodeDiv = document.getElementById('auth-code-div');
    const numberInputField = document.getElementById('auth-number-input');
    const authCodeVerificationBtn = document.getElementById('auth-code-verification-btn');

    if (name && fPhoneN && mPhoneN && bPhoneN &&
        authCodeDiv && numberInputField && authCodeVerificationBtn) {
        if (authCodeDiv.style.display === 'none') {
            numberInputField.setCustomValidity('');
        }

        numberInputField.addEventListener('input', () => {
            validatePhoneNumber(numberInputField, 6, msgList.AVAILABLE_NUMBER, msgList.INVALID_NUMBER);
        })

        authCodeVerificationBtn.addEventListener('click', async () => {
            if (numberInputField.validationMessage === msgList.AVAILABLE_NUMBER ||
                numberInputField.validationMessage === msgList.AUTH_VERIFICATION_FAILED &&
                numberInputField.value.length === 6) {
                try {
                    const smsAuthVerificationRequest = {
                        name: name.value,
                        fPhoneN: fPhoneN.value,
                        mPhoneN: mPhoneN.value,
                        bPhoneN: bPhoneN.value,
                        authNumber: numberInputField.value,
                    };

                    const response = await handleAsyncOperation(authCodeVerificationBtn, 'POST', '/signup/verification/phones/confirm', smsAuthVerificationRequest);
                    if (response) {
                        numberInputField.setCustomValidity(msgList.AUTH_VERIFICATION_COMPLETED);
                        authCodeVerificationBtn.disabled = true;
                        Timer.stop();
                    } else {
                        numberInputField.setCustomValidity(msgList.AUTH_VERIFICATION_FAILED);
                    }
                } catch (error) {
                    console.error("비동기 작업 중 오류 발생:", error);
                }
            }
        })
    }

    return numberInputField;
}

const registerUser = () => {
    const NOTIFICATION_MESSAGES = {
        AVAILABLE_NAME: '사용 가능한 이름입니다.',
        INVALID_NAME: '유효하지 않은 이름입니다. 한글만 허용되고, 2글자 이상의, 공백을 포함하지 않습니다',
        AVAILABLE_ID: '사용 가능한 아이디입니다.',
        INVALID_ID: '유효하지 않은 아이디입니다. 최소 4자 이상이어야 합니다.',
        DUPLICATE_CHECK: '중복 검사를 누르십시오.',
        DUPLICATE: '중복된 아이디입니다.',
        SUFFICIENT: '비밀번호 사용이 가능합니다.',
        WEAK: '비밀번호는 최소 8자 이상이며, 대.소문자, 숫자, 특수기호를 포함해야 합니다.',
        MATCH: '비밀번호가 일치합니다.',
        MISMATCH: '비밀번호가 일치하지 않습니다.',
        INVALID_NUMBER: '자리 숫자로 구성되어야 합니다.',
        AVAILABLE_NUMBER: '유효한 번호입니다.',
        NEEDS_AUTH_NUMBER: '인증번호 인증이 필요합니다',
        DISPATCHED_SUCCESSFULLY: '인증번호 인증 발송 처리 완료되었습니다',
        AUTH_VERIFICATION_REQUIRED: '인증번호 확인이 필요합니다.' ,
        AUTH_VERIFICATION_FAILED: '인증번호 실패, 확인 중 아이디 or 휴대폰 번호가 일치하지 않습니다',
        AUTH_VERIFICATION_COMPLETED: '인증번호 확인이 완료되었습니다.',
    };

    const signupForm = document.getElementById('signup-form');
    const confirmedName = validateName(NOTIFICATION_MESSAGES);
    const confirmedId = validateDuplicateId(NOTIFICATION_MESSAGES, handleAsyncOperation);
    const processedPwdInfo = validatePassword(NOTIFICATION_MESSAGES);
    const verCodeInfo = sendVerificationCode(NOTIFICATION_MESSAGES, handleAsyncOperation);
    const numberInputField = verifyAuthCode(NOTIFICATION_MESSAGES, handleAsyncOperation, verCodeInfo.smsAuthRequest);

    // 'click'는 폼 제출 전에 조건을 확인
    signupForm.addEventListener('click', (event) => {
        if (confirmedName) {
            if (confirmedName.validationMessage === NOTIFICATION_MESSAGES.AVAILABLE_NAME) {
                confirmedName.setCustomValidity('');
            }
        }
        if (confirmedId) {
            if (confirmedId.validationMessage === NOTIFICATION_MESSAGES.AVAILABLE_ID) {
                confirmedId.setCustomValidity('');
            }
        }
        if (processedPwdInfo) {
            if (processedPwdInfo.pwd.validationMessage === NOTIFICATION_MESSAGES.SUFFICIENT) {
                processedPwdInfo.pwd.setCustomValidity('');

            }
            if (processedPwdInfo.pwdConfirm.validationMessage === NOTIFICATION_MESSAGES.MATCH) {
                processedPwdInfo.pwdConfirm.setCustomValidity('');
            }
        }
        if (verCodeInfo) {
            if (verCodeInfo.fPhoneN) {
                if (verCodeInfo.fPhoneN.validationMessage === NOTIFICATION_MESSAGES.AVAILABLE_NUMBER) {
                    if (verCodeInfo.authCodeGenerationBtn.innerText === '인증번호 발송') {
                        verCodeInfo.fPhoneN.setCustomValidity(NOTIFICATION_MESSAGES.NEEDS_AUTH_NUMBER);
                    } else {
                        verCodeInfo.fPhoneN.setCustomValidity(NOTIFICATION_MESSAGES.DISPATCHED_SUCCESSFULLY);
                    }
                }
                else if (verCodeInfo.fPhoneN.validationMessage === NOTIFICATION_MESSAGES.DISPATCHED_SUCCESSFULLY) {
                    verCodeInfo.fPhoneN.setCustomValidity('');
                    if (verCodeInfo.mPhoneN.validationMessage === NOTIFICATION_MESSAGES.AVAILABLE_NUMBER) {
                        verCodeInfo.mPhoneN.setCustomValidity('');
                    }
                    if (verCodeInfo.bPhoneN.validationMessage === NOTIFICATION_MESSAGES.AVAILABLE_NUMBER) {
                        verCodeInfo.bPhoneN.setCustomValidity('');
                    }
                }
            }
        }
        if (numberInputField) {
            if (numberInputField.validationMessage === NOTIFICATION_MESSAGES.AUTH_VERIFICATION_COMPLETED) {
                numberInputField.setCustomValidity('');
            }
        }
    });

    signupForm.addEventListener('submit', (event) => {

    });
}

document.addEventListener('DOMContentLoaded', registerUser);