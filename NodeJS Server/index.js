var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);

connectUsers = {};

var port = process.env.PORT || 3000;

app.get("/", function (req, res) {
  res.sendFile(__dirname + "/index.html");
});

io.on("connection", socket => {
  socket.on("connect user", data => {
    console.log("user connected id: %d status: %s ", data.userId, data.status);
    socket.nickname = data.userId;
    connectUsers[socket.nickname] = socket;
  });

  socket.on("check", data => {
    if (!connectUsers[data.userId]) {
      socket.nickname = data.userId;
      connectUsers[socket.nickname] = socket;
      console.log(
        "user connected id: %d status: %s ",
        data.userId,
        data.status
      );
    }
  });

  socket.on("COMPANY MESSAGE", data => {
    console.log("User Id: %d Company Id :%d", data.userId, data.companyId);

    connectUsers[data.userId].join(data.companyId);
  });

  socket.on("COMPANY MESSAGE CHAT", data => {
    socket.broadcast.to(data.companyId).emit("COMPANY MESSAGE CHAT", {
      senderId: data.senderId,
      image: data.image,
      name: data.name,
      message: data.message
    });
  });

  socket.on("PRIVATE MESSAGE", data => {
    var receiverId = data.receiverId;
    var senderId = data.senderId;
    if (connectUsers[receiverId]) {
      connectUsers[receiverId].emit("PRIVATE MESSAGE", {
        name: data.name,
        userImage: data.userImage,
        message: data.message,
        date: data.date,
        senderId: data.senderId,
        conversationId: data.conversationId,
        receiverId: data.receiverId,
        status: "true",
        messageType: "text"
      });
    }
  });

  socket.on("POST NOTIFICATION", data => {
    var userIds = data.userIds.split(",");

    for (i = 1; i < userIds.length; i++) {
      userId = userIds[i];
      if (connectUsers[userId] != null) {
        connectUsers[userId].emit("POST NOTIFICATION", {
          postId: data.postId,
          companyName: data.companyName,
          content: "Bir gönderi paylaştı"
        });
      }
    }
  });

  socket.on("ON TYPING", typing => {
    io.emit("on typing", typing);
  });
  socket.on("disconnect", data => {
    if (!socket.nickname) return;
    delete connectUsers[socket.nickname];
    console.log("user disconnect");
  });
});

server.listen(port, function () {
  console.log("Server %d portunda dinlemeye başladı", port);
});
