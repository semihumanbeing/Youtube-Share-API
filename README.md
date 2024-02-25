# Youtube-Share-API

### 배포방법
- Qclass가 컴파일되었는지 확인
- commit and push
- GCP에 접속
- ./api-deploy
- ---

## checklist
- [x]  Spring security
- [x]  Spring data JPA / queryDSL
- [x]  User
    - [x]  Login
    - [x]  Logout
    - [x]  register
    - [ ]  kakao login
- [x]  Websocket Chat
    - [x]  websocket / redis
    - [x]  message
- [x]  Chatroom
    - [x]  create
    - [x]  update (title and password)
    - [x]  delete
    - [x]  getFromUser
    - [x]  getAll (page)
- [x]  Playlist
    - [X]  create
    - [x]  subscribe (SSE)
    - [x]  sync (SSE)
- [x] Video
    - [x]  addToPlaylist
    - [x]  getCurrent (ws)
    - [x]  getNext (ws)
    - [x]  deleteOne
    - [x]  getByChatroomId
     
