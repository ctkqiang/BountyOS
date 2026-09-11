package com.bountyos.ui.ide

import android.content.Context
import com.bountyos.domain.model.ScriptLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 一次脚本执行的结果。
 */
data class ExecutionResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
)

/**
 * 通过本机解释器执行脚本。
 *
 * 脚本写入缓存目录的临时文件后交给 [ScriptLanguage.command] 解释器，
 * stdout / stderr 并行读取以避免子进程缓冲区写满死锁。执行在 IO 线程，
 * 绝不阻塞 UI。
 */
@Singleton
class CodeExecutor @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    suspend fun execute(code: String, language: ScriptLanguage): ExecutionResult =
        withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, "ide").apply { mkdirs() }
            val script = File(dir, "script.${language.extension}")
            script.writeText(code)
            try {
                val process = try {
                    ProcessBuilder(language.command, script.absolutePath).start()
                } catch (e: IOException) {
                    throw IOException(
                        "${language.displayName} interpreter not found (${language.command})",
                        e,
                    )
                }
                val stdout = async { process.inputStream.bufferedReader().use { it.readText() } }
                val stderr = async { process.errorStream.bufferedReader().use { it.readText() } }
                val exitCode = process.waitFor()
                ExecutionResult(
                    stdout = stdout.await(),
                    stderr = stderr.await(),
                    exitCode = exitCode,
                )
            } finally {
                script.delete()
            }
        }
}
