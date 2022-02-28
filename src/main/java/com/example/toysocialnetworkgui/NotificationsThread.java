package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import javafx.application.Platform;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationsThread extends Thread {
    Controller controller;
    MainViewController mainViewController;

    public NotificationsThread(Controller controller) {
        this.controller = controller;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    private void verifyNotifications() {
        controller.subscribedEvents(controller.getCurrentUser()).forEach(event -> {
            long timeDifferenceInSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), event.getDate());
            long timeDifferenceInMinutes = timeDifferenceInSeconds / 60;
            long secondsRemaind = timeDifferenceInSeconds % 60;

            if (timeDifferenceInSeconds >= 0) {
                if (timeDifferenceInMinutes == 1 && secondsRemaind == 0) {
                    Platform.runLater(() -> {
                        mainViewController.addNotification("Mai este un minut pana la " + event.getName() + "!");
                        mainViewController.makeExclamationImageVisible();
                    });
                } else if (timeDifferenceInMinutes == 2 && secondsRemaind == 0) {
                    Platform.runLater(() -> {
                        mainViewController.addNotification("Mai sunt 2 minute pana la " + event.getName() + "!");
                        mainViewController.makeExclamationImageVisible();
                    });
                } else if (timeDifferenceInMinutes == 3 && secondsRemaind == 0) {
                    Platform.runLater(() -> {
                        mainViewController.addNotification("Mai sunt 3 minute pana la " + event.getName() + "!");
                        mainViewController.makeExclamationImageVisible();
                    });
                } else if (timeDifferenceInMinutes == 4 && secondsRemaind == 0) {
                    Platform.runLater(() -> {
                        mainViewController.addNotification("Mai sunt 4 minute pana la " + event.getName() + "!");
                        mainViewController.makeExclamationImageVisible();
                    });
                } else if (timeDifferenceInMinutes == 5 && secondsRemaind == 0) {
                    Platform.runLater(() -> {
                        mainViewController.addNotification("Mai sunt 5 minute pana la " + event.getName() + "!");
                        mainViewController.makeExclamationImageVisible();
                    });
                } else if (timeDifferenceInMinutes == 10 && secondsRemaind == 0) {
                    Platform.runLater(() -> {
                        mainViewController.addNotification("Mai sunt 10 minute pana la " + event.getName() + "!");
                        mainViewController.makeExclamationImageVisible();
                    });
                }
            }
        });
    }

    public void run() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                verifyNotifications();
            }
        }, 0, 1000);
    }
}
