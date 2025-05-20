create database blog;
use blog;

INSERT INTO users (username, password, email, avatar, role)
VALUES 
    ('admin', 'lenhat123', 'admin@example.com', 'default_avatar.jpg', 'ADMIN'),
    ('soobin', 'lenhat123', 'soobin@gmail.com', 'avatar-soobin.jpg', 'USER'),
    ('rose', 'lenhat123', 'rose@gmail.com', 'avatar-rose.jpg', 'USER'),
    ('mtp', 'lenhat123', 'mtp@gmail.com', 'avatar-mtp.jpg', 'USER'),
    ('minzy', 'lenhat123', 'minzy@gmail.com', 'avatar-minzy.jpg', 'USER');

INSERT INTO posts (title, content, author_id, created_at, image_url)
VALUES ('Từ nay xin được gọi là diễn viên Soobin! 😂', 'Lần đầu chạm ngõ điện ảnh! 🎬 Hồi hộp quá mọi người ơi. Đây là một thử thách mới mà Soobin muốn khám phá. Mọi người đoán xem Soobin sẽ vào vai gì trong phim này nha 😉', 2, NOW(), 'photo-02.jpg'),
('Khi bạn quá đẹp trai nên bị "bỏ rơi"!', 'Ai đó có thấy "homie" nào "bùng kèo" trà đá của Tùng không? Ra nhận lỗi ngay để Tùng còn "tha thứ" nè! 😜 Trà đá sắp hết rồi đó nha! #BungKeoTraDa #NhanLoiDi #SonTung', 4, NOW(), 'photo-01.jpg');