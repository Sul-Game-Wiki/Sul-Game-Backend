//package org.sejong.sulgamewiki.controller;
//
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.sejong.sulgamewiki.util.log.LogMonitoringInvocation;
//import org.sejong.sulgamewiki.service.NotificationService;
//import org.sejong.sulgamewiki.object.Notification;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api")
//@Tag(
//    name = "알림 관리 API",
//    description = "알림 관리 API 제공"
//)
//public class NotificationController {
//    @Autowired
//    private NotificationService notificationService;
//
//    @GetMapping("/members/{memberId}/notifications")
//    @LogMonitoringInvocation
//    public Page<Notification> getNotificationsByMemberId(@PathVariable Long memberId, Pageable pageable) {
//        return notificationService.getNotificationsByMemberId(memberId, pageable);
//    }
//}