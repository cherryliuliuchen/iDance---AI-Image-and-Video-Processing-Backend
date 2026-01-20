# Animate Anyone Video Generator – backend overview

> The purpose is to let users upload an image, choose an animation template, and generate a short video using [Alibaba DashScope Animate Anyone](https://bailian.console.aliyun.com/cn-beijing/?tab=api#/api/?type=model&url=2786464).
> If you want to experience the Animate Anyone video generation flow, you can try the live demo here[iDance Animate Anyone] (https://livestockholm.com/)


> A Spring Boot backend that handles **image upload**, **image detection**, **video generation**, and **task status polling** via DashScope APIs.  
> This is a backend-focused project to practice API integration, async task handling, and database design. PostgreSQL is used.

---

## Table of Contents
- [Features](#features)
- [Requirement documents](#requirement-documents)
    - [External APIs](#external-apis)
    - [Requirements](#requirements)
- [API Endpoints](#api-endpoints)
- [Contact](#contact)

---

## Features
- Upload user images to **DashScope OSS**.
- Run **image detection** before video generation.
- Submit **Animate Anyone** video generation tasks.
- Poll task status and return final video URL.
- Uses **Spring Boot**, **JDBC**, **Flyway**, **PostgreSQL**.
- No frontend included, backend API only.

---

# Requirement documents

## External APIs

All API requests are made using **HTTP REST APIs**.

| API Function | Endpoint Example | Purpose |
|-------------|------------------|---------|
| Upload Policy | `https://dashscope.aliyuncs.com/api/v1/uploads` | Get OSS upload policy and upload user image. |
| Image Detect | `POST /api/v1/services/aigc/image2video/aa-detect` | Check if image is valid for Animate Anyone. |
| Video Synthesis | `POST /api/v1/services/aigc/image2video/video-synthesis/` | Submit async video generation task. |
| Task Status | `GET /api/v1/tasks/{taskId}` | Check generation status and get video URL. |

---

## Requirements
1. **Upload Image**
    - Receive image from frontend.
    - Upload image to DashScope OSS.
    - Save `ossUrl` in database.

2. **Image Detection**
    - Call `aa-detect` with `ossUrl`.
    - Save detection result.
    - If failed, return error to user.

3. **Submit Video Generation**
    - Use `ossUrl` and `templateId`.
    - Call `video-synthesis`.
    - Save `taskId` and status.

4. **Get Task Result**
    - Poll DashScope task API.
    - Update status and save `videoUrl`.
    - Return result to frontend.

---

## Data Processing Logic

Image Upload → OSS URL  
OSS URL → Image Detection  
Detection Pass → Video Generation Task  
Task ID → Status Polling → Video URL

---

## API Endpoints

### Template list

| Method | Endpoint | Description |
|------|---------|-------------|
| GET | `/api/generation/templates` | Returns available animation templates. |

---

### Create generation task

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | `/api/generations` | Upload image or use `ossUrl`, detect image, submit video task. |

---

### Get generation status

| Method | Endpoint | Description |
|------|---------|-------------|
| GET | `/api/generations/{generationId}` | Returns task status and video URL if ready. |

---

## Contact
If you have any questions or feedback, feel free to contact me:


> Email: cherryliuliuchen@gmail.com