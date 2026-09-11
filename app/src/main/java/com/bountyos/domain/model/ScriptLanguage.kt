package com.bountyos.domain.model

/**
 * 可在 IDE 中执行的脚本语言。
 *
 * [command] 是本机解释器命令名，[extension] 用于临时脚本文件后缀。
 * 语言名（Ruby / Python / Bash / JavaScript）为专有名词，不本地化。
 */
enum class ScriptLanguage(
    val displayName: String,
    val command: String,
    val extension: String,
) {
    RUBY("Ruby", "ruby", "rb"),
    PYTHON("Python", "python3", "py"),
    BASH("Bash", "bash", "sh"),
    JAVASCRIPT("JavaScript", "node", "js"),
}
