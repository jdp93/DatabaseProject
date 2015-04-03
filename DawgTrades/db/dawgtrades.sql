#
#This is the Schema for the DawgTrades database
#

DROP TABLE IF EXISTS Membership;
DROP TABLE IF EXISTS Attribute;
DROP TABLE IF EXISTS AttributeType;
DROP TABLE IF EXISTS Bid;
DROP TABLE IF EXISTS Auction;
DROP TABLE IF EXISTS Item;
DROP TABLE IF EXISTS Category;
DROP TABLE IF EXISTS ExperienceReport;
DROP TABLE IF EXISTS RegisteredUser;

#
# Table definition for table 'RegisteredUser'
#
CREATE TABLE RegisteredUser (
  Id              INT           UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
  Username        VARCHAR(255)  NOT NULL UNIQUE,
  Password        VARCHAR(255)  NOT NULL,
  FirstName       VARCHAR(255)  NOT NULL,
  LastName        VARCHAR(255)  NOT NULL,
  Email           VARCHAR(255)  NOT NULL,
  Phone           VARCHAR(255),
  CanText         BOOLEAN       NOT NULL,
  IsAdmin         BOOLEAN       NOT NULL
) ENGINE=InnoDB;

#
# Table definition for tabel 'ExperienceReport' which is an association class that
# has a 'many to many' relationship between 'RegisteredUser' as parent-child class
#
CREATE TABLE ExperienceReport (
  Id              INT           UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  ReviewerId      INT           UNSIGNED NOT NULL,
  ReviewedId      INT           UNSIGNED NOT NULL,
  Rating          INT           UNSIGNED NOT NULL,
  Report          VARCHAR(255)  NOT NULL,
  Date            DATETIME      NOT NULL,
  FOREIGN KEY     (ReviewerId) REFERENCES RegisteredUser(Id) ON DELETE CASCADE,
  FOREIGN KEY     (ReviewedId) REFERENCES RegisteredUser(Id) ON DELETE CASCADE
) ENGINE=InnoDB;

#
# Table definition for table 'Category'
#
CREATE TABLE Category(
  Id              INT           UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  ParentId        INT           UNSIGNED,
  Name            VARCHAR(255)  NOT NULL UNIQUE
) ENGINE=InnoDB;

#
# Table definition for table 'Item'
#
CREATE TABLE Item (
  Id              INT           UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  OwnerId         INT           UNSIGNED NOT NULL,
  CategoryId      INT           UNSIGNED NOT NULL,
  Name            VARCHAR(255)  NOT NULL UNIQUE,
  Description      VARCHAR(500),
  FOREIGN KEY     (OwnerId) REFERENCES RegisteredUser(Id) ON DELETE CASCADE,
  FOREIGN KEY     (CategoryId) REFERENCES Category(Id) ON DELETE CASCADE
) ENGINE=InnoDB;

#
# Table definition for table 'Auction'
#
CREATE TABLE Auction (
  Id              INT           UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  ItemId          INT           UNSIGNED NOT NULL,
  MinPrice        DECIMAL(12,2) UNSIGNED NOT NULL,
  Expiration      DATETIME      NOT NULL,
  FOREIGN KEY     (ItemId) REFERENCES Item(Id) ON DELETE CASCADE
) ENGINE=InnoDB;

#
# Table definition for table 'Bid', which
# is a many-to-many relationship
# between RegisteredUser and Auction
#
CREATE TABLE Bid(
  Id              INT           UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  UserId          INT           UNSIGNED NOT NULL,
  AuctionId       INT           UNSIGNED NOT NULL,
  Amount          INT           UNSIGNED NOT NULL,
  FOREIGN KEY     (UserId) REFERENCES RegisteredUser(Id) ON DELETE CASCADE,
  FOREIGN KEY     (AuctionId) REFERENCES Auction(Id) ON DELETE CASCADE
) ENGINE=InnoDB;

#
# Table definition for table 'AttributeType'
#
CREATE TABLE AttributeType(
  Id              INT           UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  CategoryId      INT           UNSIGNED NOT NULL,
  Name            VARCHAR(255)  NOT NULL,
  FOREIGN KEY     (CategoryId) REFERENCES Category(Id) ON DELETE CASCADE
) ENGINE=InnoDB;

#
# Table definition for table 'Attribute'
#
CREATE TABLE Attribute(
  Id              INT           UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  ItemId          INT           UNSIGNED NOT NULL,
  AttributeTypeId INT           UNSIGNED NOT NULL,
  Value           VARCHAR(255)  NOT NULL,
  FOREIGN KEY     (ItemId) REFERENCES Item(Id) ON DELETE CASCADE,
  FOREIGN KEY     (AttributeTypeId) REFERENCES AttributeType(Id) ON DELETE CASCADE
) ENGINE=InnoDB;

#
# Table definition for table 'Membership'
#
CREATE TABLE Membership(
  Id              INT           UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  Price           DECIMAL(12,2) UNSIGNED NOT NULL,
  Date            DATETIME      NOT NULL
) ENGINE=InnoDB;

#
#Create a single RegisteredUser as an admin
#
INSERT INTO RegisteredUser ( Username, Password, FirstName, LastName, Email, Phone, CanText, IsAdmin) 
values ( 'admin', 'admin', 'admin', 'admin', 'tradedawgs@gmail.com', null, false, true )