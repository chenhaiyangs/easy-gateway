/*
 Navicat Premium Data Transfer

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : localhost
 Source Database       : easy-gateway

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : utf-8

 Date: 01/12/2019 20:32:06 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `gateway_api`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_api`;
CREATE TABLE `gateway_api` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) NOT NULL COMMENT 'api名称',
  `path` varchar(20) DEFAULT NULL COMMENT '暴露给第三方的路径，如： /user',
  `description` varchar(200) DEFAULT NULL COMMENT '针对该api的一个描述',
  `login_api` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否是登陆型api，验签等系统会使用。默认为非登陆型api',
  `scopes` varchar(100) DEFAULT NULL COMMENT 'api所在的域,用户token组件会使用到',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `create_by` varchar(30) NOT NULL COMMENT '创建者',
  `update_by` varchar(30) NOT NULL COMMENT '最后更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='api元信息列表';

-- ----------------------------
--  Table structure for `gateway_circuit_breaker`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_circuit_breaker`;
CREATE TABLE `gateway_circuit_breaker` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `api_id` int(11) NOT NULL COMMENT '关联的api的id',
  `limit` int(10) NOT NULL COMMENT '熔断器配置-每一个服务路径uri的最大的信号量',
  `percent` int(3) NOT NULL COMMENT '当错误率小于%percent，则不进行熔断',
  `force_close` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启强制关闭容器功能，0不开启，1，开启，默认不开启',
  `retries_time` int(11) NOT NULL DEFAULT '5000' COMMENT '如果已经触发了熔断，那么，多久以后放一个请求过去（单位：ms）默认为五秒',
  `timeout` int(11) NOT NULL COMMENT '超时时间',
  `forbidden` tinyint(2) NOT NULL DEFAULT '0' COMMENT '组件是否被禁用，0 ，未禁用，1，已禁用。默认为未禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `update_by` varchar(30) DEFAULT NULL COMMENT '最后更新者',
  `create_by` varchar(30) DEFAULT NULL COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='网关的熔断器组件配置数据库';

-- ----------------------------
--  Table structure for `gateway_ip_blacklist`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_ip_blacklist`;
CREATE TABLE `gateway_ip_blacklist` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_id` int(11) NOT NULL COMMENT '关联的api的Id',
  `blacklist` varchar(1024) NOT NULL COMMENT '黑名单ip列表',
  `forbidden` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否是禁用，0，未禁用，1，禁用。默认是未禁用状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `update_by` varchar(30) NOT NULL COMMENT '最后更新者',
  `create_by` varchar(30) NOT NULL COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='针对单个api的ip黑名单列表';

-- ----------------------------
--  Table structure for `gateway_ip_whitelist`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_ip_whitelist`;
CREATE TABLE `gateway_ip_whitelist` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_id` int(11) NOT NULL COMMENT '关联的api的Id',
  `whitelist` varchar(1024) NOT NULL COMMENT '黑名单ip列表',
  `forbidden` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否是禁用，0，未禁用，1，禁用。默认是未禁用状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `update_by` varchar(30) NOT NULL COMMENT '最后更新者',
  `create_by` varchar(30) NOT NULL COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='针对单个api的ip白名单组件配置集，如果有api配置了白名单组件，则只有请求的ip地址在该api白名单的请求才可以访问';

-- ----------------------------
--  Table structure for `gateway_mock`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_mock`;
CREATE TABLE `gateway_mock` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增组件Id',
  `api_id` int(11) NOT NULL COMMENT '关联的apiId',
  `body` varchar(512) NOT NULL COMMENT 'mock返回的body，当开启mock时，将此body塞到http response body',
  `forbidden` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否是禁用，0，未禁用，1，禁用。默认是未禁用状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `update_by` varchar(30) NOT NULL COMMENT '最后更新者',
  `create_by` varchar(30) NOT NULL COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='网关的api的mock返回数据';

-- ----------------------------
--  Table structure for `gateway_ratelimit`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_ratelimit`;
CREATE TABLE `gateway_ratelimit` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `api_id` int(11) NOT NULL COMMENT '关联的api_id',
  `limit_body` varchar(512) NOT NULL COMMENT '限流策略，是一个map，可以配置多种限流方案，key表示多少秒，value表示在key时间内该api允许多少个请求路过',
  `type` tinyint(2) NOT NULL DEFAULT '1' COMMENT '限流方式，1:分布式限流，使用redis。2:单机限流',
  `forbidden` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否是禁用，0，未禁用，1，禁用。默认是未禁用状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `update_by` varchar(30) NOT NULL COMMENT '最后更新者',
  `create_by` varchar(30) NOT NULL COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='api网关限流组件';

-- ----------------------------
--  Table structure for `gateway_response_header`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_response_header`;
CREATE TABLE `gateway_response_header` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增组件Id',
  `api_id` int(11) NOT NULL COMMENT '关联的apiId',
  `headers` varchar(512) NOT NULL COMMENT '针对单个api-对http响应中添加header。k-v，存一个map',
  `forbidden` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否是禁用，0，未禁用，1，禁用。默认是未禁用状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `update_by` varchar(30) NOT NULL COMMENT '最后更新者',
  `create_by` varchar(30) NOT NULL COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='网关后置处理器->给响应添加http header';

-- ----------------------------
--  Table structure for `gateway_restful_route`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_restful_route`;
CREATE TABLE `gateway_restful_route` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `api_id` int(11) NOT NULL COMMENT 'api的id',
  `route_uri` varchar(100) DEFAULT NULL COMMENT '组件路由的真实后端的uri。如没有，则取request的uri',
  `route_list` varchar(2000) NOT NULL COMMENT 'api的具体后端ip:port列表，是一个map，key 为 ip:port,value为权重',
  `type` tinyint(100) NOT NULL DEFAULT '1' COMMENT '路由类型：1，随机权重，2，ip_hash',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `update_by` varchar(30) NOT NULL COMMENT '最后更新者',
  `create_by` varchar(30) NOT NULL COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='restful调用的路由组件，路由到后端具体的restful地址产生调用';

-- ----------------------------
--  Table structure for `gateway_sign_config`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_sign_config`;
CREATE TABLE `gateway_sign_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `api_id` int(11) NOT NULL COMMENT '关联的api的id',
  `forbidden` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否是禁用，0，未禁用，1，禁用。默认是未禁用状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `update_by` varchar(30) NOT NULL COMMENT '最后更新者',
  `create_by` varchar(30) NOT NULL COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='网关的请求签名组件配置数据库';

-- ----------------------------
--  Table structure for `gateway_token_config`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_token_config`;
CREATE TABLE `gateway_token_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `api_id` int(11) NOT NULL COMMENT '关联的api的id',
  `expire` int(11) NOT NULL COMMENT 'token的实效时间，单位：秒',
  `forbidden` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否是禁用，0，未禁用，1，禁用。默认是未禁用状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `update_by` varchar(30) NOT NULL COMMENT '最后更新者',
  `create_by` varchar(30) NOT NULL COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='网关的token组件的配置';

SET FOREIGN_KEY_CHECKS = 1;
