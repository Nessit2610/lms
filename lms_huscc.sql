CREATE TABLE `lms_account` (
  `id` varchar(36) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_USER__ON_USERNAME` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_account_roles` (
  `Account_id` varchar(36) DEFAULT NULL,
  `roles_name` varchar(255) DEFAULT NULL,
  KEY `FK_LMS_ACCOUNT_ROLES_ON_ACCOUNT` (`Account_id`),
  KEY `FK_LMS_ACCOUNT_ROLES_ON_ROLES_NAME` (`roles_name`),
  CONSTRAINT `FK_LMS_ACCOUNT_ROLES_ON_ACCOUNT` FOREIGN KEY (`Account_id`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `FK_LMS_ACCOUNT_ROLES_ON_ROLES_NAME` FOREIGN KEY (`roles_name`) REFERENCES `lms_role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_acount_device` (
  `id` varchar(36) NOT NULL,
  `accountId` varchar(36) DEFAULT NULL,
  `deviceId` varchar(255) DEFAULT NULL,
  `fcmDeviceToken` varchar(255) DEFAULT NULL,
  `deviceType` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_LMS_ACOUNT_DEVICE_ON_ACCOUNTID` (`accountId`),
  CONSTRAINT `FK_LMS_ACOUNT_DEVICE_ON_ACCOUNTID` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_chapter` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `order` int DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `lessonId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `createdBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_lesson_id` (`lessonId`),
  CONSTRAINT `fk_lesson_id` FOREIGN KEY (`lessonId`) REFERENCES `lms_lesson` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_comment` (
  `id` varchar(36) NOT NULL,
  `accountId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `chapterId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `courseId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `lessonMaterialId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  `updateDateAt` datetime(6) DEFAULT NULL,
  `postId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_comment_account` (`accountId`),
  KEY `fk_comment_chapter` (`chapterId`),
  KEY `fk_comment_course` (`courseId`),
  KEY `fk_comment_lesson_material` (`lessonMaterialId`),
  KEY `FK6oc9rl31h8els5h7op77ltm95` (`postId`),
  CONSTRAINT `FK6oc9rl31h8els5h7op77ltm95` FOREIGN KEY (`postId`) REFERENCES `lms_post` (`id`),
  CONSTRAINT `fk_comment_account` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `fk_comment_chapter` FOREIGN KEY (`chapterId`) REFERENCES `lms_chapter` (`id`),
  CONSTRAINT `fk_comment_course` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`),
  CONSTRAINT `fk_comment_lesson_material` FOREIGN KEY (`lessonMaterialId`) REFERENCES `lms_lesson_material` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_comment_read_status` (
  `id` varchar(36) NOT NULL,
  `commentId` varchar(36) DEFAULT NULL,
  `accountId` varchar(36) DEFAULT NULL,
  `isRead` tinyint(1) DEFAULT NULL,
  `commentType` enum('COMMENT','REPLY','COMMENT_POST','COMMENT_REPLY_POST') DEFAULT NULL,
  `commentReplyId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_account` (`accountId`),
  KEY `fk_comment` (`commentId`),
  KEY `FK4adlq7bbhyog8wt6doc7973wu` (`commentReplyId`),
  CONSTRAINT `FK4adlq7bbhyog8wt6doc7973wu` FOREIGN KEY (`commentReplyId`) REFERENCES `lms_comment_reply` (`id`),
  CONSTRAINT `fk_account` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `fk_comment` FOREIGN KEY (`commentId`) REFERENCES `lms_comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_comment_reply` (
  `id` varchar(36) NOT NULL,
  `ownerAccountId` varchar(36) DEFAULT NULL,
  `replyAccountId` varchar(36) DEFAULT NULL,
  `chapterId` varchar(36) DEFAULT NULL,
  `courseId` varchar(36) DEFAULT NULL,
  `commentId` varchar(36) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  `updateDateAt` datetime(6) DEFAULT NULL,
  `postId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_comment_reply_owner_account` (`ownerAccountId`),
  KEY `fk_comment_reply_reply_account` (`replyAccountId`),
  KEY `fk_comment_reply_chapter` (`chapterId`),
  KEY `fk_comment_reply_course` (`courseId`),
  KEY `fk_comment_reply_comment` (`commentId`),
  KEY `FKj589cp2b4hj8qox1o3t2wh1ay` (`postId`),
  CONSTRAINT `fk_comment_reply_chapter` FOREIGN KEY (`chapterId`) REFERENCES `lms_chapter` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_comment_reply_comment` FOREIGN KEY (`commentId`) REFERENCES `lms_comment` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_reply_course` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_comment_reply_owner_account` FOREIGN KEY (`ownerAccountId`) REFERENCES `lms_account` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_comment_reply_reply_account` FOREIGN KEY (`replyAccountId`) REFERENCES `lms_account` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FKj589cp2b4hj8qox1o3t2wh1ay` FOREIGN KEY (`postId`) REFERENCES `lms_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_confirmation_token` (
  `id` varchar(36) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `createdAt` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `isVerify` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_course` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `startDate` datetime(6) DEFAULT NULL,
  `endDate` datetime(6) DEFAULT NULL,
  `major` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `learningDurationType` varchar(255) DEFAULT NULL,
  `teacherId` varchar(36) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  `feeType` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_MAJOR` (`major`),
  KEY `IDX_LMS_COURSE_TEACHERID` (`teacherId`),
  CONSTRAINT `FK_LMS_COURSE_ON_TEACHERID` FOREIGN KEY (`teacherId`) REFERENCES `lms_teacher` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_document` (
  `id` varchar(36) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` longtext,
  `status` varchar(255) DEFAULT NULL,
  `majorId` varchar(36) DEFAULT NULL,
  `accountId` varchar(36) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_document_major` (`majorId`),
  KEY `fk_document_account` (`accountId`),
  CONSTRAINT `fk_document_account` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `fk_document_major` FOREIGN KEY (`majorId`) REFERENCES `lms_major` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_group` (
  `id` varchar(36) NOT NULL,
  `createdAt` datetime(6) DEFAULT NULL,
  `teacherId` varchar(36) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrfnldyynxoi3cneskfn7w6ct9` (`teacherId`),
  CONSTRAINT `FKrfnldyynxoi3cneskfn7w6ct9` FOREIGN KEY (`teacherId`) REFERENCES `lms_teacher` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_invalidated_token` (
  `id` varchar(255) NOT NULL,
  `expiryTime` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_join_class_requests` (
  `id` varchar(36) NOT NULL,
  `createdAt` datetime(6) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `courseId` varchar(36) NOT NULL,
  `studentId` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKf5q2c371aaki931081xo2dpps` (`courseId`),
  KEY `FKakifs76ktnpq1dviphcw7e47y` (`studentId`),
  CONSTRAINT `FKakifs76ktnpq1dviphcw7e47y` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`),
  CONSTRAINT `FKf5q2c371aaki931081xo2dpps` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_lesson` (
  `id` varchar(36) NOT NULL,
  `courseId` varchar(36) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `order` int DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_LESSON_COURSE` (`courseId`),
  CONSTRAINT `FK_LMS_LESSON_ON_COURSEID` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_lesson_material` (
  `id` varchar(36) NOT NULL,
  `path` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `lessonId` varchar(36) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_LESSON_MATERIAL_LESSONID` (`lessonId`),
  CONSTRAINT `FK_LMS_LESSON_MATERIAL_ON_LESSONID` FOREIGN KEY (`lessonId`) REFERENCES `lms_lesson` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_lesson_quiz` (
  `id` varchar(36) NOT NULL,
  `lessonId` varchar(36) DEFAULT NULL,
  `question` varchar(255) DEFAULT NULL,
  `option` varchar(255) DEFAULT NULL,
  `answer` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_LESSON_QUIZ_LESSONID` (`lessonId`),
  CONSTRAINT `FK_LMS_LESSON_QUIZ_ON_LESSONID` FOREIGN KEY (`lessonId`) REFERENCES `lms_lesson` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_major` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted_by` varchar(255) DEFAULT NULL,
  `deleted_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_messages` (
  `id` varchar(36) NOT NULL,
  `sendAccountId` varchar(36) DEFAULT NULL,
  `receiveAccountId` varchar(36) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` text,
  `createAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_send_account` (`sendAccountId`),
  KEY `fk_receive_account` (`receiveAccountId`),
  CONSTRAINT `fk_receive_account` FOREIGN KEY (`receiveAccountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `fk_send_account` FOREIGN KEY (`sendAccountId`) REFERENCES `lms_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_notifications` (
  `id` varchar(36) NOT NULL,
  `receiveAccountId` varchar(36) NOT NULL,
  `commentId` varchar(36) DEFAULT NULL,
  `messageId` varchar(36) DEFAULT NULL,
  `type` enum('COMMENT','MESSAGE','COMMENT_REPLY','CHAT_MESSAGE','JOIN_CLASS_PENDING','JOIN_CLASS_REJECTED','JOIN_CLASS_APPROVED','POST_CREATED','POST_COMMENT','POST_COMMENT_REPLY') DEFAULT NULL,
  `isRead` tinyint(1) DEFAULT '0',
  `description` text,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `commentReplyId` varchar(36) DEFAULT NULL,
  `chatMessageId` varchar(255) DEFAULT NULL,
  `joinClassRequestId` varchar(36) DEFAULT NULL,
  `postId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_notification_receive_account` (`receiveAccountId`),
  KEY `fk_notification_message` (`messageId`),
  KEY `fk_notification_comment` (`commentId`),
  KEY `fk_notification_comment_reply` (`commentReplyId`),
  KEY `FKk6d4o9a4p9dubmhom050xrsv` (`joinClassRequestId`),
  KEY `FKq066lodn8cotajn5fvu0feb2o` (`postId`),
  CONSTRAINT `fk_notification_comment` FOREIGN KEY (`commentId`) REFERENCES `lms_comment` (`id`),
  CONSTRAINT `fk_notification_comment_reply` FOREIGN KEY (`commentReplyId`) REFERENCES `lms_comment_reply` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_notification_message` FOREIGN KEY (`messageId`) REFERENCES `lms_messages` (`id`),
  CONSTRAINT `fk_notification_receive_account` FOREIGN KEY (`receiveAccountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `FKk6d4o9a4p9dubmhom050xrsv` FOREIGN KEY (`joinClassRequestId`) REFERENCES `lms_join_class_requests` (`id`),
  CONSTRAINT `FKq066lodn8cotajn5fvu0feb2o` FOREIGN KEY (`postId`) REFERENCES `lms_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_payment` (
  `paymentId` varchar(255) NOT NULL,
  `countryCode` varchar(255) DEFAULT NULL,
  `createTime` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `postalCode` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `totalPrice` float NOT NULL,
  `transactionFee` float NOT NULL,
  `courseId` varchar(36) DEFAULT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`paymentId`),
  KEY `FK63ueuyrcvab7h0rt8rp5sq16m` (`courseId`),
  KEY `FKhf9lh1askc0tw6bsmbvfngi6l` (`studentId`),
  CONSTRAINT `FK63ueuyrcvab7h0rt8rp5sq16m` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`),
  CONSTRAINT `FKhf9lh1askc0tw6bsmbvfngi6l` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_pending_payment` (
  `paymentId` varchar(255) NOT NULL,
  `completed` bit(1) NOT NULL,
  `course_id` varchar(36) DEFAULT NULL,
  `student_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`paymentId`),
  KEY `FKcicr8h9nb8kysk5rycvj5wbhq` (`course_id`),
  KEY `FKm0nxs79bm16yxky229onbcotq` (`student_id`),
  CONSTRAINT `FKcicr8h9nb8kysk5rycvj5wbhq` FOREIGN KEY (`course_id`) REFERENCES `lms_course` (`id`),
  CONSTRAINT `FKm0nxs79bm16yxky229onbcotq` FOREIGN KEY (`student_id`) REFERENCES `lms_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_permission` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_post` (
  `id` varchar(36) NOT NULL,
  `createdAt` datetime(6) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `text` text,
  `title` varchar(255) DEFAULT NULL,
  `groupId` varchar(36) DEFAULT NULL,
  `teacherId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtfklmh7u6wocpsgrp34xsx2q8` (`groupId`),
  KEY `FKab38ox4ry5jo8ieg1ylr44r7g` (`teacherId`),
  CONSTRAINT `FKab38ox4ry5jo8ieg1ylr44r7g` FOREIGN KEY (`teacherId`) REFERENCES `lms_teacher` (`id`),
  CONSTRAINT `FKtfklmh7u6wocpsgrp34xsx2q8` FOREIGN KEY (`groupId`) REFERENCES `lms_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_post_file` (
  `id` varchar(36) NOT NULL,
  `fileType` varchar(255) DEFAULT NULL,
  `fileUrl` varchar(255) DEFAULT NULL,
  `postId` varchar(36) DEFAULT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrn0hs3aln7ihcgjslwrunx9t2` (`postId`),
  CONSTRAINT `FKrn0hs3aln7ihcgjslwrunx9t2` FOREIGN KEY (`postId`) REFERENCES `lms_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_role` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_role_permission` (
  `Role_name` varchar(255) DEFAULT NULL,
  `permission_name` varchar(255) DEFAULT NULL,
  KEY `FK_LMS_ROLE_PERMISSION_ON_PERMISSION_NAME` (`permission_name`),
  KEY `FK_LMS_ROLE_PERMISSION_ON_ROLE_NAME` (`Role_name`),
  CONSTRAINT `FK_LMS_ROLE_PERMISSION_ON_PERMISSION_NAME` FOREIGN KEY (`permission_name`) REFERENCES `lms_permission` (`name`),
  CONSTRAINT `FK_LMS_ROLE_PERMISSION_ON_ROLE_NAME` FOREIGN KEY (`Role_name`) REFERENCES `lms_role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_student` (
  `id` varchar(36) NOT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `majorId` varchar(36) DEFAULT NULL,
  `accountId` varchar(36) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_LMS_STUDENT_ON_ACCOUNTID` (`accountId`),
  KEY `fk_student_major` (`majorId`),
  CONSTRAINT `FK_LMS_STUDENT_ON_ACCOUNTID` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `fk_student_major` FOREIGN KEY (`majorId`) REFERENCES `lms_major` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_student_course` (
  `id` varchar(36) NOT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  `courseId` varchar(36) DEFAULT NULL,
  `registrationDate` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_STUDENT_COURSE_COURSEID` (`courseId`),
  KEY `IDX_STUDENT_COURSE_STUDENTID` (`studentId`),
  CONSTRAINT `FK_LMS_STUDENT_COURSE_ON_COURSEID` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`),
  CONSTRAINT `FK_LMS_STUDENT_COURSE_ON_STUDENTID` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_student_document` (
  `id` varchar(36) NOT NULL,
  `documentId` varchar(36) DEFAULT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  `createAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_student_document_document` (`documentId`),
  KEY `fk_student_document_student` (`studentId`),
  CONSTRAINT `fk_student_document_document` FOREIGN KEY (`documentId`) REFERENCES `lms_document` (`id`),
  CONSTRAINT `fk_student_document_student` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_student_group` (
  `id` varchar(36) NOT NULL,
  `joinAt` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `groupId` varchar(36) DEFAULT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK416tvrdnpqofmyg40cvgki40a` (`groupId`),
  KEY `FK38wcyflqxe0woknfla369wi3j` (`studentId`),
  CONSTRAINT `FK38wcyflqxe0woknfla369wi3j` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`),
  CONSTRAINT `FK416tvrdnpqofmyg40cvgki40a` FOREIGN KEY (`groupId`) REFERENCES `lms_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_student_lesson_chapter_progress` (
  `id` varchar(36) NOT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  `chapterId` varchar(36) DEFAULT NULL,
  `isCompleted` tinyint(1) DEFAULT NULL,
  `completeAt` datetime DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_progress_student` (`studentId`),
  KEY `fk_progress_chapter` (`chapterId`),
  CONSTRAINT `fk_progress_chapter` FOREIGN KEY (`chapterId`) REFERENCES `lms_chapter` (`id`),
  CONSTRAINT `fk_progress_student` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_student_lesson_progress` (
  `id` varchar(36) NOT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  `lessonId` varchar(36) DEFAULT NULL,
  `isCompleted` bit(1) DEFAULT NULL,
  `completeAt` datetime DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_STUDENT_LESSON_PROGRESS_LESSONID` (`lessonId`),
  KEY `IDX_STUDENT_LESSON_PROGRESS_STUDENTID` (`studentId`),
  CONSTRAINT `FK_LMS_STUDENT_LESSON_PROGRESS_ON_LESSONID` FOREIGN KEY (`lessonId`) REFERENCES `lms_lesson` (`id`),
  CONSTRAINT `FK_LMS_STUDENT_LESSON_PROGRESS_ON_STUDENTID` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_teacher` (
  `id` varchar(36) NOT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `userId` varchar(36) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_LMS_TEACHER_ON_USERID` (`userId`),
  CONSTRAINT `FK_LMS_TEACHER_ON_USERID` FOREIGN KEY (`userId`) REFERENCES `lms_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_test_in_group` (
  `id` varchar(36) NOT NULL,
  `createdAt` timestamp NULL DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `expiredAt` timestamp NULL DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `groupId` varchar(36) DEFAULT NULL,
  `startedAt` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjatheue0p87a0x6ubgi4tru4i` (`groupId`),
  CONSTRAINT `FKjatheue0p87a0x6ubgi4tru4i` FOREIGN KEY (`groupId`) REFERENCES `lms_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_test_question` (
  `id` varchar(36) NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `correctAnswers` varchar(255) DEFAULT NULL,
  `options` text,
  `type` varchar(255) DEFAULT NULL,
  `testId` varchar(36) DEFAULT NULL,
  `point` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqe1bgm8ofxw46i7fmxb394id3` (`testId`),
  CONSTRAINT `FKqe1bgm8ofxw46i7fmxb394id3` FOREIGN KEY (`testId`) REFERENCES `lms_test_in_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_test_student_answer` (
  `id` varchar(36) NOT NULL,
  `answer` varchar(255) DEFAULT NULL,
  `questionId` varchar(36) DEFAULT NULL,
  `resultId` varchar(36) DEFAULT NULL,
  `correct` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcwe30aocpytmtc2iyckgaopuu` (`questionId`),
  KEY `FKdp8nmri49i75g3lmgw1ejvhk6` (`resultId`),
  CONSTRAINT `FKcwe30aocpytmtc2iyckgaopuu` FOREIGN KEY (`questionId`) REFERENCES `lms_test_question` (`id`),
  CONSTRAINT `FKdp8nmri49i75g3lmgw1ejvhk6` FOREIGN KEY (`resultId`) REFERENCES `lms_test_student_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lms_test_student_result` (
  `id` varchar(36) NOT NULL,
  `score` double NOT NULL,
  `submittedAt` datetime(6) DEFAULT NULL,
  `totalCorrect` int NOT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  `testId` varchar(36) DEFAULT NULL,
  `startedAt` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3g0x31ypwvsd3vuoln9keoa6v` (`studentId`),
  KEY `FKfnucktohl6xyhmt396xmr7243` (`testId`),
  CONSTRAINT `FK3g0x31ypwvsd3vuoln9keoa6v` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`),
  CONSTRAINT `FKfnucktohl6xyhmt396xmr7243` FOREIGN KEY (`testId`) REFERENCES `lms_test_in_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci; 