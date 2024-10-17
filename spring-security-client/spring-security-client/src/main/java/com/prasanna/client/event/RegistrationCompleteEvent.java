package com.prasanna.client.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import com.prasanna.client.entity.User;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private User user;
    private String applicationUrl;

    public RegistrationCompleteEvent(User user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
