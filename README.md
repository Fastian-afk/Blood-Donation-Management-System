# 🩸 Blood Donation Management System (BDMS)

A comprehensive JavaFX desktop application designed to streamline blood bank operations. This project features a modern UI, role-based authentication, and a live Cloud MySQL database connection.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Eclipse](https://img.shields.io/badge/Eclipse-2C2255?style=for-the-badge&logo=eclipse&logoColor=white)

## ✨ Key Features

* **☁ Cloud Connectivity:** Fully integrated with Aiven Cloud MySQL for real-time data access from any location.
* **🔐 Role-Based Access:** Distinct dashboards for **Admins**, **Hospital Staff**, **Donors**, and **Recipients**.
* **📊 Live Inventory:** Real-time tracking of blood units (A+, B-, O+, etc.).
* **🎨 Modern UI:** Custom CSS implementation for a clean, clinical aesthetic (Navy/Red/White theme).
* **🔄 Automated Workflow:** From 'Pending Donation' to 'Approved Unit' inventory logic.

## 🛠 Tech Stack

* **Language:** Java (JDK 21)
* **GUI Framework:** JavaFX (Standard Library)
* **Database:** MySQL (Hosted on Aiven Cloud)
* **IDE:** Eclipse

## 🚀 How to Run

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/Blood-Donation-Management-System.git](https://github.com/YOUR_USERNAME/Blood-Donation-Management-System.git)
    ```
2.  **Import to Eclipse:**
    * File > Open Projects from File System > Select the folder.
3.  **Configure Database:**
    * Open `src/app/database/DatabaseConnection.java`.
    * Update the `DB_USER` and `DB_PASSWORD` with your MySQL credentials.
4.  **Run:**
    * Right-click `src/app/Main.java` -> Run As -> Java Application.

## 📸 Screenshots


---
**Author:** Imaad Fazal

**Course:** Software Design and Analysis [2025]
