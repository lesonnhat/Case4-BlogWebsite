create database blog;
use blog;

INSERT INTO users (username, password, email, avatar)
VALUES ('soobin', 'lenhat123', 'soobin@gmail.com', 'media/avatar-soobin.jpg'),
('rose', 'lenhat123', 'rose@gmail.com', 'media/avatar-rose.jpg'),
('mtp', 'lenhat123', 'mtp@gmail.com', 'media/avatar-mtp.jpg'),
('minzy', 'lenhat123', 'minzy@gmail.com', 'media/avatar-minzy.jpg');

INSERT INTO posts (title, content, author_id, created_at, image_url)
VALUES ('Khi bạn quá đẹp trai nên bị "bỏ rơi"!', 'Ai đó có thấy "homie" nào "bùng kèo" trà đá của Tùng không? Ra nhận lỗi ngay để Tùng còn "tha thứ" nè! 😜 Trà đá sắp hết rồi đó nha! #BungKeoTraDa #NhanLoiDi #SonTung', 1, NOW(), 'media/photo-01.jpg');