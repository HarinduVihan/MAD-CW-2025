
# 1.	Introduction

##  Science More 

Science More is a reputed tuition institute that provides knowledge for students who are in grade 6 through 11. This institute operates through two branches located in Mallawagedara and Godigamuwa, offering a wide array of subjects including Science, Mathes, Sinhala, English, History, ICT, Tamil and Commerce for all 6 grade levels, conducted by 10 to 15 Teachers. 
Each class there are roughly 100 to 150 students. Approximately there are 800 students learning from Science More institute, demonstrating its significant impact and outreach within the educational landscape.

![WhatsApp Image 2025-07-12 at 14 56 48_f032842c](https://github.com/user-attachments/assets/84248051-203e-480d-8bb8-bfbb2ada749f)


## 2.	Problem definition

The operational management of Science More tuition institute is currently handled by a limited number of staff through entirely manual processes.
This approach encountered significant challenges with a student count of approximately 800 individuals across two branches resulting in the manual system struggles to accommodate the administrative demands associated with high student volume, especially in areas such as:
•	Maintaining accurate student and teacher records
•	Distributing and monitoring subject materials
•	Tracking assignment submissions and grading student performance individually
•	Mark student attendance 
Hence, the current system is inefficient and not scalable, which frequently causes delays, irregularities, and an increase in the workload of administrative staff. A simplified, technologically advanced solution is required for improved operational and educational goals.

## 3.	Project Objectives

The primary objective is to improve the efficiency and effectiveness of the Science More Education Institute while enhancing customer experience and workers’ wellbeing. 
Optimizing administrative processes, improving the quality-of-service delivery, and providing seamless academic support across all branches can be identified as some of major goals withing this project.  
The proposed improvements seek to elevate the overall learner experience by ensuring timely access to educational resources, transparent evaluation mechanisms, and structured communication.

## 4.	Proposed solution

Designing and developing a Tuition Management Information Android Application for Science More Education Institute. This app will help tuition centers manage students and teachers, track attendance using QR codes, and provide learning materials. The application will support three types of users: Admin, Teachers, and Students.


## 5.	Core functionalities

Admin has the responsibility to manage Accounts and handling core management tasks

### Admin

•	Register: When the application is launched for the first time an admin can register for the system.
•	Login: Admin can login to the system with username and password.

•	Register students: Add new student profiles including personal and academic information.    

•	Update students’ details: Modify student info when necessary.

•	Delete students: Remove a student’s account.

 
•	Register Teachers: Add new Teachers profiles including personal and academic information.
                                                              
•	Assign students to teachers: Link students to specific teachers for subject.
 
•	Assign students to courses: Enroll students into subjects and grades.
 
•	View reports 

o	Attendance Reports: Monitor attendance of each student.

o	Results Reports: View academic performance, including marks and grades.

### Teacher

Teachers interact with students and manage course-specific elements.

•	Login: Teachers must login to the system with their username and password to proceed. 
 
•	Take student attendance via QR code: Teacher scans a QR code on students’ phone to mark attendance, ensuring real-time logging and validation.
 

•	Upload assignments: Add tasks or homework with deadlines and attachments according to the relevant grade and subject.
 
•	Release results: Publish grades after assessments with possible feedback.
 
•	Upload course materials: Share learning resources like PDFs.
 
•	Delete course materials: Remove outdated or incorrect materials from the database.

### Student

Students access their own academic dashboard and interact with course material:

•	View attendance: Check personal attendance history.

•	View assignments: Browse all assigned tasks with submission deadlines.

•	View results: See grades and evaluation remarks.

•	View course materials: Access study resources uploaded by teachers.
 
•	Submit assignments: Upload files and complete submissions through the platform.

•	Receive notifications: Get alerts on new assignments, messages from teachers/admin, and announcements.

## 6.	Implementation 

### Application
The proposed Tuition Management Information System was developed as two distinct Android applications to facilitate role-specific functionalities and streamline access control. This architectural decision was made to enhance system scalability, security, and usability for different user groups.

•	Admin Application: This application is for administrative purposes such as managing user accounts, monitor attendance records.  

•	Student and Teacher Application: Designed to support both educators and learners, this application enables students to access educational content, submit assignments, and view results, while teachers can manage attendance, share subject materials, and evaluate submissions.

The modular implementation ensures that each user group interacts with an optimized interface and feature set aligned with their responsibilities, contributing to improved operational efficiency and user satisfaction across the institute.
 
### Data Management Approach

Firebase is used to store and manage necessary data relevant for the institute and applications. 
This technology enables seamless synchronization of student and teacher data across applications, facilitates dynamic retrieval of learning materials, and supports QR code-based attendance tracking through structured data models. Its real-time features contribute to enhanced responsiveness, while built-in security protocols ensure data integrity and privacy for all user groups.

### Development Environment and Technology Stack

The system was developed using Android Studio, an integrated development environment (IDE) officially supported by Google for Android application development.
The core programming language utilized in this project was Java.

## 7.	Future Developments

•	Integrate Firebase Authentication for enhanced login security.

•	Include payment tracking for course fees.

•	Add real-time chat between students and teachers.

•	Enable push notifications for instant alerts.

•	Extending platform support for iOS users.

•	Implement data analytics dashboard for admins to visualize attendance and performance trends

## 8.	Roles and contributions of each member

1.	COHNDSE242F-005   	 M.K.D. Gangadara

I.	Database Implementation

II.	Student- View subjects

III.	Student-Assignments

IV.	Student QR Scan

V.	Admin View Students

VI.	Admin View Teachers

VII.	Teacher Dashboard

VIII.	Student Dashboard

IX.	Testing and QA

2.	COHNDSE242F-008  	A.H.V.M. Jayaratne

I.	Creating the class diagram

II.	Project Report

III.	Managing GitHub repository

IV.	Admin- MainActivity

V.	Admin Register

VI.	Admin Login

VII.	Student and Teacher MainActivity

VIII.	Student/Teacher Login

IX.	Student Dashboard

X.	Teacher Dashboard

3.	COHNDSE242F-019  	D.S. Jayasinghe

I.	Presentation

II.	Admin Dashboard

III.	Admin Add Student

IV.	Admin Update Student

V.	Admin Add Teacher

VI.	Admin Update Teachers

4.	COHNDSE242F-040 		W.A.U.S. Weerasooriya

I.	Managing GitHub repository

II.	Student-Assignment-results

III.	Teacher Add Assignments

IV.	Teacher Mark Assignment

V.	Teacher Add Course material

VI.	Admin Assign Teachers

VII.	Admin Assign Students

VIII.	Testing and QA

## 9.	Conclusion

The Tuition Management Information Android Application developed for the Science More Education Institute represents a significant advancement in the digital transformation of academic administration. By replacing traditional manual practices with a robust, role-based mobile system, the project effectively addresses key challenges such as student-teacher management, attendance tracking, and resource distribution.

The dual-application architecture ensures specialized access for administrators, teachers, and students, fostering an intuitive and secure user experience. Leveraging Firebase for real-time data management and Android Studio for development, the system demonstrates strong integration between frontend functionality and backend reliability.

Through collaborative development and clearly defined responsibilities, the project team successfully implemented core features aligned with institutional needs while also identifying future enhancements such as cross-platform support, secure authentication, and real-time communication tools.

Overall, this solution not only improves operational efficiency and educational effectiveness but also contributes to a more engaging, transparent, and supportive learning ecosystem—setting a foundation for scalable growth and ongoing innovation at Science More.

## 10.	Reference 

1.	Connect your App to Firebase
https://firebase.google.com/docs/database/android/start#java_1

2.	How to generate real-time reports using Firebase
https://blog.ldtalentwork.com/2020/07/20/how-to-generate-real-time-reports-using-firebase/

3.	Add data to Cloud Firestore
https://firebase.google.com/docs/firestore/manage-data/add-data
4.	Get data with Cloud Firestore
Get data with Cloud Firestore  |  https://firebase.google.com/docs/firestore/query-data/get-dataFirebase

