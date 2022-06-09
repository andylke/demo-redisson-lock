CREATE SCHEMA IF NOT EXISTS `demo`;

DROP TABLE IF EXISTS `foo`;

CREATE TABLE `foo` (
  id INT NOT NULL,
  text VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `foo` VALUES(0, ''), (1, ''), (2, ''), (3, ''), (4, ''), (5, ''), (6, '');