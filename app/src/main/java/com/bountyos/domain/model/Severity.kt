package com.bountyos.domain.model

/**
 * 规范化的严重程度。
 *
 * 该枚举采用 HackerOne 的 `severity_rating` 语义作为规范值来源，
 * 因为该字段在官方文档中被明确定义为 `none` / `low` / `medium` /
 * `high` / `critical`。
 *
 * Bugcrowd 的严重程度使用 P1-P5 优先级体系，官方文档未提供与
 * low/medium/high/critical 的一一对应关系，因此 Bugcrowd 数据
 * 归一化时映射为 [UNKNOWN]，并保留原始优先级字符串。
 */
enum class Severity {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
    UNKNOWN,
}
