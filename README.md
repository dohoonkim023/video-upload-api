# 개요
간단한 mp4 영상 업로드 및 변환을 위한 HTTP API

Use: Spring, JPA, H2, Docker, ffmpeg



# 1. 영상 업로드 및 변환 API
- [x] 파일 포맷은 mp4로 제한하고 1회에 업로드할 수 있는 영상의 최대 용량을 100MB로 제한합니다. 조건에 부합하지 않는 경우 에러를 응답합니다.

- [x] 영상의 제목을 별도로 지정할 수 있습니다.

- [x] 업로드가 완료되면 가로 사이즈가 360px인 영상 1개를 추가로 생성합니다. 이 때 원본 영상의 비율과 퀄리티를 유지합니다.

- [x] 영상의 변환 작업은 오랜 시간이 소요되므로 업로드가 완료되면 데이터 저장 후 즉시 성공으로 응답하고, 변환 작업은 비동기적으로 실행합니다.

- [x] 영상의 변환은 ffmpeg을 이용합니다.

  https://ffmpeg.org

- [x] 업로드한 영상, 변환한 영상 등의 리소스는 모두 임의의 로컬 폴더에 저장하고 API에서는 이 파일을 static resource로 제공합니다.

  e.g.) /path/to/sample video.mp4

        http://localhost:8080/path/to/sample%20video.mp4
        


# 2. 영상의 상세 정보를 조회할 수 있는 API를 제공
- [x] 제목, 생성 시간(업로드 시간), 원본 영상의 주소, 변환한 영상의 주소를 제공합니다.

- [x] filesize, width, height 명시는 선택 사항입니다.

- 요청 예시

  GET /video/{id}


- 응답 예시

  {

      "id": 123,
      "title": "This is a sample video.",
      "original": {
          "filesize": 58234223,
          "width": 900,
          "height": 500,
          "videoUrl": "http://.../video/sample.mp4"
      },
      "resized": {
        "filesize": 12831208,
        "width": 180,
        "height": 100,
        "videoUrl": "http://.../video/sample_180.mp4"
      },
      "createdAt": "2023-01-01T10:00:00+09:00"
  }
  
  
  
# 3. 업로드 직후 영상의 변환 진행률을 수시로 조회할 수 있는 API를 제공
- [ ] 요청 예시
  GET /video/{id}/progress
- 응답 예시
  {
      "id": 123,
      "progress"
  }
  
  
  
# 4. 영상 업로드 후 리사이징 작업 전에 영상의 첫 번째 장면을 썸네일로 추출하여 저장
- [x] 썸네일 추출
