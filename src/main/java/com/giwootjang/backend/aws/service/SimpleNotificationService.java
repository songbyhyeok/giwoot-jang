package com.giwootjang.backend.aws.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.Optional;

@Service
public final class SimpleNotificationService {
    private static final String COUNTRY_CODE_KOREA = "+82";
    private final SnsClient snsClient;

    public SimpleNotificationService() {
        this.snsClient = SnsClient.builder()
                .region(Region.AP_NORTHEAST_1)
                .build();
    }

    public String getSNSTopicArn() {
        try {
            ListTopicsRequest request = ListTopicsRequest.builder().build();
            ListTopicsResponse response = snsClient.listTopics(request);
            //System.out.println("Status was " + result.sdkHttpResponse().statusCode() + "\n\nTopics\n\n" + result.topics());
            return response.topics().get(0).topicArn();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return "";
        }
    }

    public void subscribeSMS(final String topicArn, final String phoneNumber) {
        try {
            SubscribeRequest request = SubscribeRequest.builder()
                    .protocol("SMS")
                    .endpoint(COUNTRY_CODE_KOREA + phoneNumber)
                    .returnSubscriptionArn(true)
                    .topicArn(topicArn)
                    .build();

            SubscribeResponse response = snsClient.subscribe(request);
            //System.out.println("Subscription ARN: " + result.subscriptionArn() + "\n\n Status is " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public boolean isSubscribedPhoneNumber(final String phoneNumber) {
        try {
            ListSmsSandboxPhoneNumbersRequest request = ListSmsSandboxPhoneNumbersRequest.builder().build();
            ListSmsSandboxPhoneNumbersResponse response = snsClient.listSMSSandboxPhoneNumbers(request);

            return Optional.of(response)
                    .filter(res -> res.sdkHttpResponse().statusCode() == 200)
                    .map(res -> res.phoneNumbers().stream()
                            .filter(item -> item.status() == SMSSandboxPhoneNumberVerificationStatus.VERIFIED)
                            .anyMatch(item -> item.phoneNumber().equals(COUNTRY_CODE_KOREA + phoneNumber)))
                    .orElse(false);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return false;
        }
    }

    public boolean verifySMSMessagePublished(final String phoneNumber, final String authNumber) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .phoneNumber(COUNTRY_CODE_KOREA + phoneNumber)
                    .message("인증번호: " + authNumber)
                    .build();

            PublishResponse response = snsClient.publish(request);

            if (response.sdkHttpResponse().statusCode() != 200) {
                throw new IllegalArgumentException("메시지 발행에 실패했습니다. 상태 코드: " + phoneNumber.length());
            }

            //System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());
            return true;

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return false;
        }
    }
}
